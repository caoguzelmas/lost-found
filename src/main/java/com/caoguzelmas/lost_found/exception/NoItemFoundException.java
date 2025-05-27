package com.caoguzelmas.lost_found.exception;

public class NoItemFoundException extends Exception {

    public NoItemFoundException(String message) {
        super(message);
    }

    public NoItemFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
