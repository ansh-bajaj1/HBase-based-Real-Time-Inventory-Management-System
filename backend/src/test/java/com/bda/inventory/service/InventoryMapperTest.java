package com.bda.inventory.service;

import com.bda.inventory.dto.InventoryItemResponse;
import com.bda.inventory.model.InventoryItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

class InventoryMapperTest {

    private final InventoryMapper mapper = new InventoryMapper();

    @Test
    void shouldMarkLowStockWhenBelowThreshold() {
        InventoryItem item = new InventoryItem();
        item.setItemId("ITEM-1");
        item.setName("USB Cable");
        item.setCategory("Accessories");
        item.setPrice(new BigDecimal("199.99"));
        item.setStockQuantity(3);
        item.setLastUpdated(Instant.now());

        InventoryItemResponse response = mapper.toResponse(item, 5);

        Assertions.assertTrue(response.isLowStock());
        Assertions.assertEquals("ITEM-1", response.getItemId());
    }
}
