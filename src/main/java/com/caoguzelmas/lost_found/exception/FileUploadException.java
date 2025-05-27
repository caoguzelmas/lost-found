package com.caoguzelmas.lost_found.exception;

public class FileUploadException extends Exception {

    public FileUploadException(String message) {
        super(message);
    }

    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
