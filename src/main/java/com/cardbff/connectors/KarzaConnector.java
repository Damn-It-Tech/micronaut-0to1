package com.cardbff.connectors;

import com.cardbff.interceptor.LogMethods;
import com.cardbff.model.Customer;
import com.cardbff.model.KarzaRequestData;
import com.cardbff.model.KarzaResponseData;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.logging.Logger;

@Singleton
@LogMethods
@Primary
public class KarzaConnector implements CustomerNetworkDao {

    private static final Logger logger = Logger.getLogger(KarzaConnector.class.getName());

    @Inject
    @Client
    private HttpClient httpClient;

    @Value("${karza.api.key}")
    private String apiKey;

    @Override
    public HttpResponse<KarzaResponseData> getPanStatusFromKarza(Customer customer) {

        KarzaRequestData requestData = new KarzaRequestData(customer.getName(), customer.getPan(), customer.getDob(), "Y");

        HttpRequest<KarzaRequestData> request = HttpRequest
                .POST("https://api.karza.in/v2/pan-authentication", requestData)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header("x-karza-key", apiKey);

        try{
            return Flowable.fromCallable(() -> httpClient.toBlocking().exchange(request,KarzaResponseData.class))
                    .subscribeOn(Schedulers.io())
                    .map(HttpResponse::ok).onErrorResumeNext(throwable -> {
                        logger.severe("Error calling third-party service: " + throwable.getMessage());
                        return Flowable.error(throwable);
                    })
                    .blockingSingle().body();

        }
        catch (Exception e){
            logger.severe(e.getMessage());
            return null;
        }
    }
}
