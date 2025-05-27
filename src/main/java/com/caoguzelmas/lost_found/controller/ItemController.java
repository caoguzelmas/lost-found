package com.caoguzelmas.lost_found.controller;

import com.caoguzelmas.lost_found.model.dto.LostItemDTO;
import com.caoguzelmas.lost_found.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping("/upload")
    public ResponseEntity upload(@RequestParam("file") MultipartFile file) {
        return null;
    }

    @GetMapping
    public ResponseEntity<List<LostItemDTO>> getAllLostItems() {
        return null;
    }

    // upload
    // getAllItems

    // item service
}
