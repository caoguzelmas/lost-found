package com.caoguzelmas.lost_found.strategy;

import com.caoguzelmas.lost_found.model.dto.ParsedItemDataDTO;
import com.caoguzelmas.lost_found.util.FileParsingConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class PdfFileParserStrategy implements FileParserStrategy {

    private static final Pattern ITEM_NAME_PATTERN = Pattern.compile("^"
            + Pattern.quote(FileParsingConstants.ITEM_NAME_PREFIX) + "\\s*(.*)", Pattern.CASE_INSENSITIVE);
    private static final Pattern QUANTITY_PATTERN = Pattern.compile("^"
            + Pattern.quote(FileParsingConstants.QUANTITY_PREFIX) + "\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern PLACE_PATTERN = Pattern.compile("^"
            + Pattern.quote(FileParsingConstants.PLACE_PREFIX) + "\\s*(.*)", Pattern.CASE_INSENSITIVE);

    @Override
    public List<ParsedItemDataDTO> parseFile(MultipartFile file) {
        final List<ParsedItemDataDTO> parsedItems = new ArrayList<>();
        log.info("Attempting to parse PDF file: {}", file.getOriginalFilename());

        try (InputStream inputStream = file.getInputStream();
             PDDocument document = Loader.loadPDF(file.getBytes())) {

            final PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setSortByPosition(true);
            final String textFromDocument = pdfStripper.getText(document);
            log.info("Extracted text from PDF: {}", textFromDocument);

            final String[] lines = textFromDocument.split("\\r?\\n");
            String currentItemName = null;
            Integer currentQuantity = null;
            String currentPlace = null;
            int lineNumber = 0;

            for (String line : lines) {
                lineNumber++;
                final String trimmedLine = line.trim();

                if (trimmedLine.isEmpty()) {
                    continue;
                }

                final Matcher itemNameMatcher = ITEM_NAME_PATTERN.matcher(trimmedLine);
                final Matcher quantityMatcher = QUANTITY_PATTERN.matcher(trimmedLine);
                final Matcher placeMatcher = PLACE_PATTERN.matcher(trimmedLine);

                if (itemNameMatcher.matches()) {
                    if (currentItemName != null && currentQuantity != null && currentPlace != null) {
                        final ParsedItemDataDTO parsedItem = ParsedItemDataDTO
                                .builder()
                                .itemName(currentItemName)
                                .quantity(currentQuantity)
                                .sourceFileInfo(file.getOriginalFilename())
                                .place(currentPlace).build();

                        parsedItems.add(parsedItem);
                    }

                    currentItemName = itemNameMatcher.group(1).trim();
                    currentQuantity = null;
                    currentPlace = null;

                    log.info("Line {}: Found ItemName: {}", lineNumber, currentItemName);
                } else if (quantityMatcher.matches()) {
                    try {
                        currentQuantity = Integer.parseInt(quantityMatcher.group(1).trim());
                        log.info("Line {}: Found Quantity: {}", lineNumber, currentQuantity);
                    } catch (NumberFormatException e) {
                        log.warn("Line {}: Invalid number format for Quantity for item: {}", lineNumber, currentItemName);
                        currentQuantity = null;
                    }
                } else if (placeMatcher.matches() && currentItemName != null && currentQuantity != null) {
                    currentPlace = placeMatcher.group(1).trim();
                    log.info("Line {}: Found Place: {} for item: {}", lineNumber, currentPlace, currentItemName);
                    final ParsedItemDataDTO parsedItem = ParsedItemDataDTO.builder()
                            .itemName(currentItemName)
                            .quantity(currentQuantity)
                            .sourceFileInfo(file.getOriginalFilename())
                            .place(currentPlace).build();

                    parsedItems.add(parsedItem);
                    currentItemName = null;
                    currentQuantity = null;
                    currentPlace = null;
                } else {
                    log.trace("Line {}: Skipping line for file {}: {}", lineNumber, file.getOriginalFilename(), trimmedLine);
                }
            }

            if (currentItemName != null && currentQuantity != null && currentPlace != null) {
                final ParsedItemDataDTO parsedItem = ParsedItemDataDTO.builder()
                        .itemName(currentItemName)
                        .quantity(currentQuantity)
                        .place(currentPlace)
                        .sourceFileInfo(file.getOriginalFilename()).build();
                parsedItems.add(parsedItem);
            }
        } catch (IOException e) {
            log.error("IOException during PDF parsing for file: {}", file.getOriginalFilename(), e);
            // TODO
        } catch (Exception e) {
            log.error("Unexpected error during PDF parsing for file: {}", file.getOriginalFilename(), e);
            // TODO
        }

        log.info("Successfully parsed PDF file: {}", file.getOriginalFilename());

        return parsedItems;
    }

    @Override
    public boolean supports(String fileContentType, String fileName) {
        if (FileParsingConstants.TYPE_PDF.equalsIgnoreCase(fileContentType)) {
            return true;
        }

        return fileName != null && fileName.toLowerCase().endsWith(FileParsingConstants.EXTENSION_PDF);
    }
}
