package com.innowise.orderservice.exception;

public class ResourceNotFoundException extends ServiceException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}