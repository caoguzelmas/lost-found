package com.caoguzelmas.lost_found.util;

public class ErrorMessageConstants {

    private ErrorMessageConstants() {
        throw new IllegalStateException("Utility class can not be instantiated");
    }

    // Generic File Parsing Error Messages
    public static final String ERROR_MESSAGE_FILE_EMPTY = "Uploaded file is empty or null";
    public static final String ERROR_MESSAGE_UNSUPPORTED_FILE_TYPE = "Unsupported file type: {} for the file: {} ";
    public static final String ERROR_MESSAGE_PARSING_ERROR = "Failed to parse file: {}";
    public static final String ERROR_MESSAGE_NO_EXTENSION_FOUND = "No extension found for file: {}";
    public static final String ERROR_MESSAGE_INVALID_QUANTITY_FORMAT = "Invalid quantity format in file :{}";
    public static final String ERROR_MESSAGE_GENERAL_INTERNAL_ERROR = "An unexpected error occurred while parsing file: {}";
    public static final String ERROR_MESSAGE_VALIDATION_ERROR = "Validation error occurred while parsing file: {}";
    public static final String ERROR_MESSAGE_MAX_UPLOAD_SIZE_EXCEEDED = "Max upload size exceeded";

    // Generic Claim Request Error Messages
    public static final String ERROR_MESSAGE_NO_ITEM_FOUND = "ItemLocationInventory with given ID: {} is not found";
    public static final String ERROR_MESSAGE_REQUESTED_NUMBER_OF_QUANTITY_CANNOT_BE_LESS_OR_EQUAL_ZERO = "Claimed quantity ({}) cannot be less than or equal to 0";
    public static final String ERROR_MESSAGE_INVALID_NUMBER_OF_QUANTITY = "Claimed quantity ({}) cannot be more than available quantity ({})";
    public static final String ERROR_MESSAGE_USER_NOT_FOUND = "Claiming user with given ID: {} is not found";



}
