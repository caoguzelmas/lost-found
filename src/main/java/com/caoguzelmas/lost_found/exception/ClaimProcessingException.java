package com.caoguzelmas.lost_found.exception;

public class ClaimProcessingException extends Exception {
    public ClaimProcessingException(String message) {
        super(message);
    }

    public ClaimProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
