package com.caoguzelmas.lost_found.strategy;

import com.caoguzelmas.lost_found.model.dto.ParsedItemDataDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileParserStrategy {

    List<ParsedItemDataDTO> parseFile(final MultipartFile file);

    boolean supports(final String fileContentType, final String fileName);
}
