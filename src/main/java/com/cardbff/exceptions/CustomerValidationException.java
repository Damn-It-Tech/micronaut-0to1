package com.cardbff.exceptions;

import io.micronaut.http.HttpStatus;
import lombok.Getter;

@Getter
public class CustomerValidationException extends RuntimeException {
    private final HttpStatus status;

    public CustomerValidationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}


