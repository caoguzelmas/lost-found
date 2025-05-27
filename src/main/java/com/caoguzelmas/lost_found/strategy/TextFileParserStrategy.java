package com.caoguzelmas.lost_found.strategy;

import com.caoguzelmas.lost_found.model.dto.ParsedItemDataDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class TextFileParserStrategy implements FileParserStrategy {
    @Override
    public List<ParsedItemDataDTO> parseFile(MultipartFile file) {
        return List.of();
    }

    @Override
    public boolean supports(String fileContentType, String fileName) {
        return false;
    }
}
