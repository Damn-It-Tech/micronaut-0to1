package com.cardbff.connectors;

import com.cardbff.model.Customer;
import com.cardbff.model.KarzaResponseData;
import io.micronaut.http.HttpResponse;
import io.reactivex.rxjava3.core.Maybe;

public interface CustomerNetworkDao {
    public HttpResponse<KarzaResponseData> getPanStatusFromKarza(Customer customer);

    public Maybe<HttpResponse<KarzaResponseData>> getPanStatusFromKarzaUsingMaybe(Customer customer);
}
