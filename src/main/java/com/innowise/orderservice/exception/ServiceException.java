package com.innowise.orderservice.exception;

public class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }
}