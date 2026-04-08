package com.bda.inventory.controller;

import com.bda.inventory.dto.AddItemRequest;
import com.bda.inventory.dto.InventoryItemResponse;
import com.bda.inventory.dto.UpdateStockRequest;
import com.bda.inventory.service.CsvBulkUploadService;
import com.bda.inventory.service.InventoryCommandService;
import com.bda.inventory.service.InventoryQueryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class InventoryController {

    private final InventoryCommandService commandService;
    private final InventoryQueryService queryService;
    private final CsvBulkUploadService csvBulkUploadService;

    public InventoryController(InventoryCommandService commandService,
                               InventoryQueryService queryService,
                               CsvBulkUploadService csvBulkUploadService) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.csvBulkUploadService = csvBulkUploadService;
    }

    @PostMapping("/item/add")
    public ResponseEntity<Map<String, String>> addItem(@Valid @RequestBody AddItemRequest request) {
        commandService.addItem(request);
        return ResponseEntity.ok(Map.of("message", "Item event published"));
    }

    @PutMapping("/item/update/{id}")
    public ResponseEntity<Map<String, String>> updateStock(@PathVariable("id") String itemId,
                                                           @Valid @RequestBody UpdateStockRequest request) {
        commandService.updateStock(itemId, request);
        return ResponseEntity.ok(Map.of("message", "Stock update event published"));
    }

    @DeleteMapping("/item/{id}")
    public ResponseEntity<Map<String, String>> deleteItem(@PathVariable("id") String itemId) {
        commandService.deleteItem(itemId);
        return ResponseEntity.ok(Map.of("message", "Delete event published"));
    }

    @GetMapping("/item/{id}")
    public ResponseEntity<InventoryItemResponse> getItem(@PathVariable("id") String itemId) {
        return ResponseEntity.ok(queryService.getItem(itemId));
    }

    @GetMapping("/items/all")
    public ResponseEntity<List<InventoryItemResponse>> getAllItems() {
        return ResponseEntity.ok(queryService.getAllItems());
    }

    @GetMapping("/items/low-stock")
    public ResponseEntity<List<InventoryItemResponse>> getLowStockItems() {
        return ResponseEntity.ok(queryService.getLowStockItems());
    }

    @PostMapping("/bulk-upload")
    public ResponseEntity<Map<String, Object>> bulkUpload(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(csvBulkUploadService.upload(file));
    }
}
