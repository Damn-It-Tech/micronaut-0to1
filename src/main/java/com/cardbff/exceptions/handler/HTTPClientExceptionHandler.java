package com.cardbff.exceptions.handler;

import com.cardbff.exceptions.HTTPClientException;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;


@Produces
@Requires(classes = {HTTPClientExceptionHandler.class, ExceptionHandler.class})
@Singleton
@Primary
public class HTTPClientExceptionHandler implements ExceptionHandler<HTTPClientException, HttpResponse<String>> {

    @Override
    public HttpResponse<String> handle(HttpRequest request, HTTPClientException exception) {
        return HttpResponse.serverError("Some HTTPClient Exception occurred: " + exception.getMessage());
    }
}
