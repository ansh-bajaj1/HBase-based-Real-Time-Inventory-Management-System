package com.bda.inventory.exception;

public class ItemNotFoundException extends InventoryException {

    public ItemNotFoundException(String itemId) {
        super("Item not found: " + itemId);
    }
}
