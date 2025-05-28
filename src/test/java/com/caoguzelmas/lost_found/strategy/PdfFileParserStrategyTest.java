package com.caoguzelmas.lost_found.strategy;

import com.caoguzelmas.lost_found.model.dto.ParsedItemDataDTO;
import com.caoguzelmas.lost_found.util.constants.FileParsingConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PdfFileParserStrategyTest {

    @InjectMocks
    private PdfFileParserStrategy pdfFileParserStrategy;

    @Nested
    @DisplayName("Unit tests for supports method")
    class SupportsMethodTests {

        @Test
        @DisplayName("Returns true when Valid PDF format received")
        void testSupports_whenValidPdfFileReceived_shouldReturnTrue() throws IOException {
            assertTrue(pdfFileParserStrategy.supports(FileParsingConstants.TYPE_PDF, loadPdfFromResources("lost-items-valid.pdf").getOriginalFilename()));
        }

        @Test
        @DisplayName("Returns false when Invalid File format received")
        void testSupports_whenInvalidFileReceived_shouldReturnFalse() {
            assertFalse(pdfFileParserStrategy.supports("WRONG_CONTENT", "somefile.txt"));
        }
    }

    @Nested
    @DisplayName("Unit tests for parseFile method")
    class ParseFileMethodTests {

        @Test
        @DisplayName("Validate parse successfully when Valid PDF File received")
        void testParseFile_whenValidPdfWithMultipleItems_thenParseSuccessfully() throws IOException {
            // Given / When
            final List<ParsedItemDataDTO> result = pdfFileParserStrategy.parseFile(loadPdfFromResources("lost-items-valid.pdf"));

            // Then
            assertNotNull(result);
            assertEquals(5, result.size());
        }
    }

    private MultipartFile loadPdfFromResources(String resourceFileName) throws IOException {
        final String path = "pdf-files-for-tests/" + resourceFileName;
        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
        if (inputStream == null) {
            throw new IOException("Cannot find resource file: " + path);
        }

        return new MockMultipartFile(
                resourceFileName,
                resourceFileName,
                FileParsingConstants.TYPE_PDF,
                inputStream
        );
    }
}
