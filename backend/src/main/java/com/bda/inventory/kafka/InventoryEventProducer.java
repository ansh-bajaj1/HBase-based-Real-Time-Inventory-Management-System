package com.bda.inventory.kafka;

import com.bda.inventory.exception.InventoryException;
import com.bda.inventory.model.InventoryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(InventoryEventProducer.class);

    private final KafkaTemplate<String, InventoryEvent> kafkaTemplate;

    @Value("${inventory.kafka.topic}")
    private String topic;

    public InventoryEventProducer(KafkaTemplate<String, InventoryEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(InventoryEvent event) {
        try {
            kafkaTemplate.send(topic, event.getItemId(), event);
            logger.info("Published event {} for item {}", event.getChangeType(), event.getItemId());
        } catch (Exception ex) {
            throw new InventoryException("Failed to publish inventory event", ex);
        }
    }
}
