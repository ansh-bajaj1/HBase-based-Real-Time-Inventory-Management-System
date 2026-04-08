package com.bda.inventory.kafka;

import com.bda.inventory.model.ChangeType;
import com.bda.inventory.model.InventoryEvent;
import com.bda.inventory.model.InventoryItem;
import com.bda.inventory.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(InventoryEventConsumer.class);

    private final InventoryRepository inventoryRepository;

    public InventoryEventConsumer(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Retryable(retryFor = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1500, multiplier = 2))
    @KafkaListener(topics = "${inventory.kafka.topic}", groupId = "inventory-consumer-group")
    public void consume(InventoryEvent event) {
        logger.info("Consumed inventory event {} for item {}", event.getChangeType(), event.getItemId());

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
