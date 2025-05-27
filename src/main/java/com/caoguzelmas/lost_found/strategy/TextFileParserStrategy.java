package com.caoguzelmas.lost_found.strategy;

import com.caoguzelmas.lost_found.model.dto.ParsedItemDataDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class TextFileParserStrategy implements FileParserStrategy {
    @Override
    public List<ParsedItemDataDTO> parseFile(MultipartFile file) {
        return List.of();
    }

    @Override
    public boolean supports(String fileExtension) {
        return false;
    }
}
