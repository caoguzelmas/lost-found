package com.caoguzelmas.lost_found.service;

import com.caoguzelmas.lost_found.exception.FileUploadException;
import com.caoguzelmas.lost_found.exception.UnsupportedFileTypeException;
import com.caoguzelmas.lost_found.helper.TestDataBuilder;
import com.caoguzelmas.lost_found.model.dto.LostItemDTO;
import com.caoguzelmas.lost_found.model.dto.ParsedItemDataDTO;
import com.caoguzelmas.lost_found.model.entity.ItemLocationInventory;
import com.caoguzelmas.lost_found.repository.ItemLocationInventoryRepository;
import com.caoguzelmas.lost_found.strategy.FileParserStrategy;
import com.caoguzelmas.lost_found.util.constants.ErrorMessageConstants;
import com.caoguzelmas.lost_found.util.constants.FileParsingConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemLocationInventoryRepository inventoryRepository;

    @Mock
    private FileParserStrategy mockTextParser;

    @Mock
    private FileParserStrategy mockPdfParser;

    @Spy
    private List<FileParserStrategy> fileParsers = new ArrayList<>();

    @InjectMocks
    private ItemService itemService;

    private MultipartFile mockTextFile;
    private MultipartFile mockPdfFile;
    private MultipartFile mockUnsupportedFile;
    private MultipartFile mockEmptyFile;
    private MultipartFile mockFileNoExtension;

    @BeforeEach
    void setUp() {
        fileParsers.clear();
        fileParsers.add(mockTextParser);
        fileParsers.add(mockPdfParser);
    }

    @Nested
    @DisplayName("Unit Tests for processUploadedFile method")
    class ProcessUploadedFileTests {

        @Test
        @DisplayName("Validate process of supported .txt file successfully")
        void testProcessUploadedFile_whenValidTXTFileUploaded_shouldProcessSuccessfully() throws UnsupportedFileTypeException, FileUploadException {
            // Given
            setupMocksForTextFile();
            final ParsedItemDataDTO parsedItem1 = TestDataBuilder.createParsedItemDataDTO("Laptop", 1, "Office");
            final ParsedItemDataDTO parsedItem2 = TestDataBuilder.createParsedItemDataDTO("Mouse", 2, "Desk");
            final List<ParsedItemDataDTO> parsedItems = Arrays.asList(parsedItem1, parsedItem2);


            when(mockTextParser.parseFile(mockTextFile)).thenReturn(parsedItems);
            when(mockTextParser.supports(anyString(), anyString())).thenReturn(true);

            final ItemLocationInventory existingItem = TestDataBuilder.createItem(1L, "Laptop", "Office", 1);
            when(inventoryRepository.findByItemNameAndPlace("Laptop", "Office")).thenReturn(Optional.of(existingItem));
            when(inventoryRepository.findByItemNameAndPlace("Mouse", "Desk")).thenReturn(Optional.empty());
            when(inventoryRepository.save(any(ItemLocationInventory.class))).thenAnswer(invocation -> invocation.getArgument(0));
            // When
            itemService.processUploadedFile(mockTextFile);
            // Then
            verify(mockTextParser, times(1)).parseFile(mockTextFile);
            verify(inventoryRepository, times(2)).save(any(ItemLocationInventory.class));

        }

        @Test
        @DisplayName("Validate process of supported .pdf file successfully")
        void testProcessUploadedFile_whenValidPDFFileUploaded_shouldProcessSuccessfully() throws UnsupportedFileTypeException, FileUploadException, IOException {
            // Given
            setupMocksForPDFFile();
            final ParsedItemDataDTO parsedItem = TestDataBuilder.createParsedItemDataDTO("Keyboard", 1, "Home");
            List<ParsedItemDataDTO> parsedItems = Collections.singletonList(parsedItem);

            when(mockPdfParser.parseFile(mockPdfFile)).thenReturn(parsedItems);
            when(inventoryRepository.findByItemNameAndPlace(anyString(), anyString())).thenReturn(Optional.empty());
            when(inventoryRepository.save(any(ItemLocationInventory.class))).thenAnswer(invocation -> invocation.getArgument(0));
            // When
            itemService.processUploadedFile(mockPdfFile);
            // Then
            verify(mockPdfParser, times(1)).parseFile(mockPdfFile);
            verify(inventoryRepository, times(1)).save(any(ItemLocationInventory.class));
        }

        @Test
        @DisplayName("Validate throws FileUploadException when empty file uploaded")
        void testProcessUploadedFile_whenEmptyFileUploaded_shouldThrowFileUploadException() {
            // Given / When
            final FileUploadException exceptionForEmptyFile = assertThrows(FileUploadException.class, () -> itemService.processUploadedFile(mockEmptyFile));
            final FileUploadException exceptionForNullFile = assertThrows(FileUploadException.class, () -> itemService.processUploadedFile(null));
            // Then
            assertTrue(exceptionForEmptyFile.getMessage().contains(ErrorMessageConstants.ERROR_MESSAGE_FILE_EMPTY));
            assertTrue(exceptionForNullFile.getMessage().contains(ErrorMessageConstants.ERROR_MESSAGE_FILE_EMPTY));
            verify(fileParsers.getFirst(), never()).parseFile(any());
        }

        @Test
        @DisplayName("Validate throws UnsupportedFileTypeException when unsupported file uploaded")
        void testProcessUploadedFile_whenUnsupportedFileUploaded_shouldThrowUnsupportedFileTypeException() {
            // Given / When
            setupMocksForUnsupportedFile();
            final UnsupportedFileTypeException exception = assertThrows(UnsupportedFileTypeException.class, () -> itemService.processUploadedFile(mockUnsupportedFile));
            // Then
            assertTrue(exception.getMessage().contains(mockUnsupportedFile.getContentType()));
        }

        @Test
        @DisplayName("Validate throws UnsupportedFileTypeException when file with no extension uploaded")
        void testProcessUploadedFile_whenFileWithNoExtension_shouldThrowUnsupportedFileTypeException() {
            setupMocksForFileNoExtension();
            // Given / When
            final UnsupportedFileTypeException exception = assertThrows(UnsupportedFileTypeException.class, () -> {
                itemService.processUploadedFile(mockFileNoExtension);
            });
            // Then
            assertTrue(exception.getMessage().contains(ErrorMessageConstants.ERROR_MESSAGE_NO_EXTENSION_FOUND.split("%s")[0].trim()));
        }
    }

    @Test
    @DisplayName("Validate throws FileUploadException when file Parser throws an error")
    void testProcessUploadedFile_whenParserThrowsRuntimeException_thenThrowFileUploadException() throws IOException {
        // Given
        setupMocksForPDFFile();
        when(mockPdfParser.parseFile(mockPdfFile)).thenThrow(new RuntimeException("Test parser error"));

        // When
        FileUploadException exception = assertThrows(FileUploadException.class, () -> {
            itemService.processUploadedFile(mockPdfFile);
        });
        //Then
        assertTrue(exception.getMessage().contains(ErrorMessageConstants.ERROR_MESSAGE_PARSING_ERROR));
        assertInstanceOf(RuntimeException.class, exception.getCause());
    }

    @Nested
    @DisplayName("Unit Tests for getAllLostItems method")
    class GetAllLostItemsTests {

        @Test
        @DisplayName("Validate returns DTO list when multiple items exist")
        void testGetAllLostItems_whenMultipleItemsExist_thenReturnListOfLostItemDTOs() {
            // Given
            ItemLocationInventory item1 = TestDataBuilder.createItem(1L, "Laptop", "Office", 5);
            ItemLocationInventory item2 = TestDataBuilder.createItem(2L, "Keyboard", "Desk", 2);
            List<ItemLocationInventory> items = Arrays.asList(item1, item2);
            when(inventoryRepository.findAll()).thenReturn(items);

            // When
            List<LostItemDTO> result = itemService.getAllLostItems();

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("Laptop", result.get(0).getItemName());
            assertEquals("Keyboard", result.get(1).getItemName());
            verify(inventoryRepository, times(1)).findAll();
        }
    }

    private void setupMocksForTextFile() {
        mockTextFile = mock(MultipartFile.class);
        when(mockTextFile.getOriginalFilename()).thenReturn("items.txt");
        when(mockTextFile.getContentType()).thenReturn(FileParsingConstants.TYPE_TEXT);
        when(mockTextFile.isEmpty()).thenReturn(false);
    }

    private void setUpParserForTextFile() {
        when(mockTextParser.supports(eq(FileParsingConstants.TYPE_TEXT), anyString())).thenReturn(true);
        when(mockTextParser.supports(eq(FileParsingConstants.TYPE_TEXT), eq("items.txt"))).thenReturn(true);
    }

    private void setupMocksForPDFFile() {
        mockPdfFile = mock(MultipartFile.class);
        when(mockPdfFile.getOriginalFilename()).thenReturn("items.pdf");
        when(mockPdfFile.getContentType()).thenReturn(FileParsingConstants.TYPE_PDF);
        when(mockPdfFile.isEmpty()).thenReturn(false);
        when(mockPdfParser.supports(eq(FileParsingConstants.TYPE_PDF), eq("items.pdf"))).thenReturn(true);
    }

    private void setUpParserForPDFFile() {
        when(mockPdfParser.supports(eq(FileParsingConstants.TYPE_PDF), anyString())).thenReturn(true);
        when(mockPdfParser.supports(eq(FileParsingConstants.TYPE_PDF), eq("items.pdf"))).thenReturn(true);
    }

    private void setupMocksForUnsupportedFile() {
        mockUnsupportedFile = mock(MultipartFile.class);
        when(mockUnsupportedFile.getOriginalFilename()).thenReturn("items.docx");
        when(mockUnsupportedFile.getContentType()).thenReturn("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        when(mockUnsupportedFile.isEmpty()).thenReturn(false);
    }

    private void setupMocksForEmptyFile() {
        mockEmptyFile = mock(MultipartFile.class);
        when(mockEmptyFile.isEmpty()).thenReturn(true);
    }

    private void setupMocksForFileNoExtension() {
        mockFileNoExtension = mock(MultipartFile.class);
        when(mockFileNoExtension.getOriginalFilename()).thenReturn("items_no_extension");
        when(mockFileNoExtension.getContentType()).thenReturn(null); // Content type da null olsun
        when(mockFileNoExtension.isEmpty()).thenReturn(false);
    }
}
