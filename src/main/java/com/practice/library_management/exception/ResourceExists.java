package com.practice.library_management.exception;

public class ResourceExists extends RuntimeException {

    public ResourceExists(String message) {
        super(message);
    }

}
