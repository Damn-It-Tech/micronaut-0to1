package com.cardbff.exceptions.handler;

import com.cardbff.exceptions.DatabaseOperationException;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

@Produces
@Requires(classes = {DatabaseOperationExceptionHandler.class, ExceptionHandler.class})
@Singleton
@Primary
public class DatabaseOperationExceptionHandler implements ExceptionHandler<DatabaseOperationException, HttpResponse<String>> {

    @Override
    public HttpResponse<String> handle(HttpRequest request, DatabaseOperationException exception) {
        return HttpResponse.serverError("Some database exception occurred: " +  exception.getMessage());
    }
}
