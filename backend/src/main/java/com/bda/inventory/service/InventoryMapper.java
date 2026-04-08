package com.bda.inventory.service;

import com.bda.inventory.dto.InventoryItemResponse;
import com.bda.inventory.model.InventoryItem;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public InventoryItemResponse toResponse(InventoryItem item, int lowStockThreshold) {
        InventoryItemResponse response = new InventoryItemResponse();
        response.setItemId(item.getItemId());
        response.setName(item.getName());
        response.setCategory(item.getCategory());
        response.setPrice(item.getPrice());
        response.setStockQuantity(item.getStockQuantity());
        response.setLastUpdated(item.getLastUpdated());
        response.setLowStock(item.getStockQuantity() <= lowStockThreshold);
        return response;
    }
}
