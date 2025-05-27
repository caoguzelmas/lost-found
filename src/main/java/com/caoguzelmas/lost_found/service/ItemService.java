package com.caoguzelmas.lost_found.service;

import com.caoguzelmas.lost_found.repository.FindingEventRepository;
import com.caoguzelmas.lost_found.repository.ItemLocationInventoryRepository;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

    private final ItemLocationInventoryRepository inventoryRepository;
    private final FindingEventRepository findingEventRepository;

    public ItemService(ItemLocationInventoryRepository inventoryRepository, FindingEventRepository findingEventRepository) {
        this.inventoryRepository = inventoryRepository;
        this.findingEventRepository = findingEventRepository;
    }
}
