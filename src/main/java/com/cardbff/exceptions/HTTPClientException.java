package com.cardbff.exceptions;


public class HTTPClientException extends RuntimeException {
    public HTTPClientException(String message) {
        super(message);
    }
}