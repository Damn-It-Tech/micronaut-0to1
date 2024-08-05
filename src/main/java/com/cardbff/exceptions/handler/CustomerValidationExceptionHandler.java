package com.cardbff.exceptions.handler;

import com.cardbff.exceptions.CustomerValidationException;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.http.server.exceptions.InternalServerException;
import jakarta.inject.Singleton;


@Produces
@Requires(classes = {CustomerValidationExceptionHandler.class, ExceptionHandler.class})
@Singleton
@Primary
public class CustomerValidationExceptionHandler implements ExceptionHandler<CustomerValidationException, HttpResponse<String>> {

    @Override
    public HttpResponse<String> handle(HttpRequest request, CustomerValidationException exception) {
        return HttpResponse.badRequest("Invalid customer data: " + exception.getMessage());
    }
}