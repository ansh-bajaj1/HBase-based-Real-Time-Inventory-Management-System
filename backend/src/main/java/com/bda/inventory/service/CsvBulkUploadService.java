package com.bda.inventory.service;

import com.bda.inventory.dto.AddItemRequest;
import com.bda.inventory.exception.InventoryException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class CsvBulkUploadService {

    private final InventoryCommandService inventoryCommandService;

    public CsvBulkUploadService(InventoryCommandService inventoryCommandService) {
        this.inventoryCommandService = inventoryCommandService;
    }

    public Map<String, Object> upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InventoryException("CSV file is empty");
        }

        int success = 0;
        int failed = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser parser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).build().parse(reader)) {

            for (CSVRecord record : parser) {
                try {
                    AddItemRequest request = new AddItemRequest();
                    request.setItemId(record.get("item_id"));
                    request.setName(record.get("name"));
                    request.setCategory(record.get("category"));
                    request.setPrice(new java.math.BigDecimal(record.get("price")));
                    request.setStockQuantity(Integer.parseInt(record.get("stock_quantity")));
                    inventoryCommandService.addItem(request);
                    success++;
                } catch (Exception rowEx) {
                    failed++;
                }
            }
        } catch (Exception ex) {
            throw new InventoryException("Failed to process CSV upload", ex);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("successful_records", success);
        response.put("failed_records", failed);
        response.put("message", "Bulk upload completed");
        return response;
    }
}
