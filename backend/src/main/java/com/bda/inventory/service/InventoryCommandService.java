package com.bda.inventory.service;

import com.bda.inventory.dto.AddItemRequest;
import com.bda.inventory.dto.UpdateStockRequest;
import com.bda.inventory.exception.ItemNotFoundException;
import com.bda.inventory.kafka.InventoryEventProducer;
import com.bda.inventory.model.ChangeType;
import com.bda.inventory.model.InventoryEvent;
import com.bda.inventory.model.InventoryItem;
import com.bda.inventory.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class InventoryCommandService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryCommandService.class);

    private final InventoryRepository inventoryRepository;
    private final InventoryEventProducer inventoryEventProducer;

    public InventoryCommandService(InventoryRepository inventoryRepository, InventoryEventProducer inventoryEventProducer) {
        this.inventoryRepository = inventoryRepository;
        this.inventoryEventProducer = inventoryEventProducer;
    }

    public void addItem(AddItemRequest request) {
        InventoryEvent event = new InventoryEvent();
        event.setItemId(request.getItemId());
        event.setName(request.getName());
        event.setCategory(request.getCategory());
        event.setPrice(request.getPrice());
        event.setStockQuantity(request.getStockQuantity());
        event.setQuantityChanged(request.getStockQuantity());
        event.setChangeType(ChangeType.ADD);
        event.setTimestamp(Instant.now());
        publishWithFallback(event);
    }

    public void updateStock(String itemId, UpdateStockRequest request) {
        InventoryItem current = inventoryRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        InventoryEvent event = new InventoryEvent();
        event.setItemId(itemId);
        event.setName(current.getName());
        event.setCategory(current.getCategory());
        event.setPrice(current.getPrice());
        event.setStockQuantity(request.getStockQuantity());
        event.setQuantityChanged(request.getStockQuantity() - current.getStockQuantity());
        event.setChangeType(ChangeType.UPDATE);
        event.setTimestamp(Instant.now());
        publishWithFallback(event);
    }

    public void deleteItem(String itemId) {
        InventoryItem current = inventoryRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        InventoryEvent event = new InventoryEvent();
        event.setItemId(itemId);
        event.setName(current.getName());
        event.setCategory(current.getCategory());
        event.setPrice(current.getPrice());
        event.setStockQuantity(0);
        event.setQuantityChanged(-current.getStockQuantity());
        event.setChangeType(ChangeType.DELETE);
        event.setTimestamp(Instant.now());
        publishWithFallback(event);
    }

    private void publishWithFallback(InventoryEvent event) {
        try {
            inventoryEventProducer.publish(event);
        } catch (Exception ex) {
            logger.warn("Kafka publish failed. Applying event directly as fallback for item {}", event.getItemId(), ex);
            applyDirectly(event);
        }
    }

    private void applyDirectly(InventoryEvent event) {
        if (event.getChangeType() == ChangeType.DELETE) {
            inventoryRepository.deleteById(event.getItemId());
        } else {
            InventoryItem item = new InventoryItem();
            item.setItemId(event.getItemId());
            item.setName(event.getName());
            item.setCategory(event.getCategory());
            item.setPrice(event.getPrice());
            item.setStockQuantity(event.getStockQuantity());
            item.setLastUpdated(event.getTimestamp());
            inventoryRepository.saveItem(item);
        }
        inventoryRepository.appendLog(event.getItemId(), event.getChangeType(), event.getQuantityChanged(), event.getTimestamp());
    }
}
