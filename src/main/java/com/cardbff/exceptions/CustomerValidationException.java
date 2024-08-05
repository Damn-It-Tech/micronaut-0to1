package com.cardbff.exceptions;

import io.micronaut.http.HttpStatus;
import lombok.Getter;

@Getter
public class CustomerValidationException extends RuntimeException {
    public CustomerValidationException(String message) {
        super(message);
    }

}


