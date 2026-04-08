package com.bda.inventory.service;

import com.bda.inventory.dto.InventoryItemResponse;
import com.bda.inventory.exception.ItemNotFoundException;
import com.bda.inventory.model.InventoryItem;
import com.bda.inventory.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryQueryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;

    @Value("${inventory.low-stock-threshold}")
    private int lowStockThreshold;

    public InventoryQueryService(InventoryRepository inventoryRepository, InventoryMapper inventoryMapper) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryMapper = inventoryMapper;
    }

    public InventoryItemResponse getItem(String itemId) {
        InventoryItem item = inventoryRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));
        return inventoryMapper.toResponse(item, lowStockThreshold);
    }

    public List<InventoryItemResponse> getAllItems() {
        return inventoryRepository.findAll().stream()
                .map(item -> inventoryMapper.toResponse(item, lowStockThreshold))
                .toList();
    }

    public List<InventoryItemResponse> getLowStockItems() {
        return inventoryRepository.findAll().stream()
                .filter(item -> item.getStockQuantity() <= lowStockThreshold)
                .map(item -> inventoryMapper.toResponse(item, lowStockThreshold))
                .toList();
    }
}
