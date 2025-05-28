package com.caoguzelmas.lost_found.util.constants;

public class ErrorMessageConstants {

    private ErrorMessageConstants() {
        throw new IllegalStateException("Utility class can not be instantiated");
    }

    // Generic File Parsing Error Messages
    public static final String ERROR_MESSAGE_FILE_EMPTY = "Uploaded file is empty or null";
    public static final String ERROR_MESSAGE_UNSUPPORTED_FILE_TYPE = "Unsupported file type: %s for the file: %s ";
    public static final String ERROR_MESSAGE_PARSING_ERROR = "Failed to parse file: %s";
    public static final String ERROR_MESSAGE_NO_EXTENSION_FOUND = "No extension found for file: %s";

    // Generic Claim Request Error Messages
    public static final String ERROR_MESSAGE_NO_ITEM_FOUND = "ItemLocationInventory with given ID: %d is not found";
    public static final String ERROR_MESSAGE_REQUESTED_NUMBER_OF_QUANTITY_CANNOT_BE_LESS_OR_EQUAL_ZERO = "Claimed quantity (%d) cannot be less than or equal to 0";
    public static final String ERROR_MESSAGE_INVALID_NUMBER_OF_QUANTITY = "Claimed quantity (%d) cannot be more than available quantity (%d)";
    public static final String ERROR_MESSAGE_CLAIMING_USER_NOT_FOUND = "Claiming user with given ID: %d is not found";

    // Generic Authentication Error Messages
    public static final String ERROR_MESSAGE_USER_NOT_FOUND = "User with the given username: %s is not found";
    public static final String ERROR_MESSAGE_JWT_TOKEN_EXPIRED = "JWT token expired: %s";
    public static final String ERROR_MESSAGE_JWT_UNEXPECTED_ERROR = "Unexpected error occurred while processing JWT: %s";
    public static final String ERROR_MESSAGE_JWT_INVALID_TOKEN = "Invalid JWT token: %s";
    public static final String ERROR_MESSAGE_JWT_TOKEN_VALIDATION_FAILED = "JWT token validation failed for user: %s";
    public static final String ERROR_MESSAGE_JWT_INVALID_CREDENTIALS = "Authentication failed: Invalid credentials.";
    public static final String ERROR_MESSAGE_JWT_AUTHENTICATION_FAILED = "Authentication failed: %s";
}
