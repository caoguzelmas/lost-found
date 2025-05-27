package com.caoguzelmas.lost_found.service;

import com.caoguzelmas.lost_found.exception.FileUploadException;
import com.caoguzelmas.lost_found.exception.UnsupportedFileTypeException;
import com.caoguzelmas.lost_found.model.dto.LostItemDTO;
import com.caoguzelmas.lost_found.model.dto.ParsedItemDataDTO;
import com.caoguzelmas.lost_found.model.entity.FindingEvent;
import com.caoguzelmas.lost_found.model.entity.ItemLocationInventory;
import com.caoguzelmas.lost_found.repository.ItemLocationInventoryRepository;
import com.caoguzelmas.lost_found.strategy.FileParserStrategy;
import com.caoguzelmas.lost_found.util.constants.ErrorMessageConstants;
import com.caoguzelmas.lost_found.util.constants.FileParsingConstants;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemService {

    private final ItemLocationInventoryRepository inventoryRepository;
    private final List<FileParserStrategy> fileParsers;

    public ItemService(ItemLocationInventoryRepository inventoryRepository, List<FileParserStrategy> fileParsers) {
        this.inventoryRepository = inventoryRepository;
        this.fileParsers = fileParsers;
    }

    @Transactional
    public void processUploadedFile(final MultipartFile file) throws FileUploadException, UnsupportedFileTypeException {
        // check if file exists
        if (file == null || file.isEmpty()) {
            log.warn(ErrorMessageConstants.ERROR_MESSAGE_FILE_EMPTY);
            throw new FileUploadException(ErrorMessageConstants.ERROR_MESSAGE_FILE_EMPTY);
        }

        String contentType = file.getContentType();
        final String originalFilename = file.getOriginalFilename();
        log.info("Processing uploaded file: {}", originalFilename);

        // if the contentType is null, determine the contentType from file extension
        if (contentType == null && file.getOriginalFilename() != null) {
            log.info("Original content type is null. Attempting to determine from the file extension for:{}", originalFilename);
            contentType = determineFileContentType(originalFilename);

            if (contentType == null) {
                log.warn("Could not determine the content type for the original filename:{}", originalFilename);
                throw new UnsupportedFileTypeException(String.format(ErrorMessageConstants.ERROR_MESSAGE_UNSUPPORTED_FILE_TYPE, "unknown file type", originalFilename));
            }
        }

        // finalContentType to be able to use it in Lambda
        final String finalContentType = contentType;
        final FileParserStrategy selectedParser = fileParsers.stream()
                .filter(parser -> parser.supports(finalContentType, originalFilename))
                .findFirst()
                .orElseThrow(() -> {
                    final String errorMessage = String.format(ErrorMessageConstants.ERROR_MESSAGE_UNSUPPORTED_FILE_TYPE, finalContentType, originalFilename);
                    log.warn(errorMessage);
                    return new UnsupportedFileTypeException(errorMessage);
                });
        log.info("Selected parser: {}", selectedParser.getClass().getSimpleName());

        try {
            final List<ParsedItemDataDTO> parsedItems = selectedParser.parseFile(file);

            if (parsedItems.isEmpty()) {
                log.info("No items found for file: {}", originalFilename);
                return;
            }
            log.info("Found {} items in file: '{}'", parsedItems.size(), originalFilename);

            for (ParsedItemDataDTO parsedItem : parsedItems) {
                saveParsedItem(parsedItem, originalFilename);
            }
            log.info("Successfully processed and stored items from file: '{}'", originalFilename);

        } catch (Exception e) {
            log.error(ErrorMessageConstants.ERROR_MESSAGE_PARSING_ERROR + originalFilename, e);
            throw new FileUploadException(ErrorMessageConstants.ERROR_MESSAGE_PARSING_ERROR + originalFilename, e);
        }
    }

    public List<LostItemDTO> getAllLostItems() {
        log.info("Retrieving all lost items");
        final List<ItemLocationInventory> items = inventoryRepository.findAll();

        final List<LostItemDTO> lostItemDTOs = items.stream()
                .map(this::convertToLostItemDTO)
                .collect(Collectors.toList());

        log.info("Successfully retrieved {} lost items", lostItemDTOs.size());
        return lostItemDTOs;
    }

    private String determineFileContentType(final String fileName) throws UnsupportedFileTypeException {
        int index = fileName.lastIndexOf(".");
        String extension = "";

        if (index > 0 && index < fileName.length() - 1) {
            extension = fileName.substring(index);
        } else {
            final String errorMessage = String.format(ErrorMessageConstants.ERROR_MESSAGE_NO_EXTENSION_FOUND, fileName);
            log.info(errorMessage);
            throw new UnsupportedFileTypeException(errorMessage);
        }

        switch (extension) {
            case FileParsingConstants.EXTENSION_PDF:
                log.info("PDF extension found for file: {}", fileName);
                return FileParsingConstants.TYPE_PDF;
            case FileParsingConstants.EXTENSION_TEXT:
                log.info("Text extension found for file: {}", fileName);
                return FileParsingConstants.TYPE_TEXT;
            default:
                log.warn("Unknown file extension: {} for file: {}. Cannot determine content type.", extension, fileName);
                return null;
        }
    }

    private void saveParsedItem(final ParsedItemDataDTO parsedItem, final String sourceFilename) {
        Optional<ItemLocationInventory> optionalExistingInventoryItem = inventoryRepository.findByItemNameAndPlace(parsedItem.getItemName(),
                parsedItem.getPlace());
        ItemLocationInventory itemLocationInventory;

        if (optionalExistingInventoryItem.isPresent()) {
            itemLocationInventory = optionalExistingInventoryItem.get();
            log.info("Updating existing ItemLocationInventory for {}-{}", parsedItem.getItemName(), parsedItem.getPlace());
        } else {
            itemLocationInventory = ItemLocationInventory.builder()
                    .itemName(parsedItem.getItemName())
                    .place(parsedItem.getPlace())
                    .totalFoundQuantity(parsedItem.getQuantity())
                    .findingEvents(new ArrayList<>())
                    .build();
            log.info("Creating new ItemLocationInventory for {}-{}", parsedItem.getItemName(), parsedItem.getPlace());
        }

        final FindingEvent findingEvent = FindingEvent.builder()
                .itemLocationInventory(itemLocationInventory)
                .foundQuantity(parsedItem.getQuantity())
                .sourceFileInfo(sourceFilename)
                .build();
        itemLocationInventory.addFindingEvent(findingEvent);

        inventoryRepository.save(itemLocationInventory);
        log.info("Saved ItemLocationInventory with ID:{} and with new FindingEvent for item: {}", itemLocationInventory.getItemLocationInventoryId(),
                itemLocationInventory.getItemName());
    }

    private LostItemDTO convertToLostItemDTO(final ItemLocationInventory entity) {
        if (entity == null) {
            return null;
        }

        return LostItemDTO.builder()
                .itemLocationInventoryId(entity.getItemLocationInventoryId())
                .itemName(entity.getItemName())
                .place(entity.getPlace())
                .totalFoundQuantity(entity.getTotalFoundQuantity())
                .build();
    }
}
