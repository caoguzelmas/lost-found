package com.caoguzelmas.lost_found.util.constants;

public class FileParsingConstants {

    private FileParsingConstants() {
        throw new IllegalStateException("Utility class can not be instantiated");
    }

    public static final String ITEM_NAME_PREFIX = "ItemName:";
    public static final String QUANTITY_PREFIX = "Quantity:";
    public static final String PLACE_PREFIX = "Place:";

    public static final String TYPE_PDF = "application/pdf";
    public static final String TYPE_TEXT = "text/plain";
    public static final String EXTENSION_PDF = ".pdf";
    public static final String EXTENSION_TEXT = ".txt";
}
