package com.bda.inventory.repository;

import com.bda.inventory.exception.InventoryException;
import com.bda.inventory.model.ChangeType;
import com.bda.inventory.model.InventoryItem;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class InventoryRepository {

    private static final byte[] DETAILS_CF = Bytes.toBytes("details");
    private static final byte[] LOGS_CF = Bytes.toBytes("logs");

    private final Connection connection;

    @Value("${inventory.hbase.inventory-table}")
    private String inventoryTable;

    @Value("${inventory.hbase.logs-table}")
    private String logsTable;

    public InventoryRepository(Connection connection) {
        this.connection = connection;
    }

    public void saveItem(InventoryItem item) {
        try (Table table = connection.getTable(TableName.valueOf(inventoryTable))) {
            Put put = new Put(Bytes.toBytes(item.getItemId()));
            put.addColumn(DETAILS_CF, Bytes.toBytes("name"), Bytes.toBytes(item.getName()));
            put.addColumn(DETAILS_CF, Bytes.toBytes("category"), Bytes.toBytes(item.getCategory()));
            put.addColumn(DETAILS_CF, Bytes.toBytes("price"), Bytes.toBytes(item.getPrice().toPlainString()));
            put.addColumn(DETAILS_CF, Bytes.toBytes("stock_quantity"), Bytes.toBytes(item.getStockQuantity()));
            put.addColumn(DETAILS_CF, Bytes.toBytes("last_updated"), Bytes.toBytes(item.getLastUpdated().toString()));
            table.put(put);
        } catch (IOException ex) {
            throw new InventoryException("Failed to write item to HBase", ex);
        }
    }

    public Optional<InventoryItem> findById(String itemId) {
        try (Table table = connection.getTable(TableName.valueOf(inventoryTable))) {
            Result result = table.get(new Get(Bytes.toBytes(itemId)));
            if (result.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(resultToItem(itemId, result));
        } catch (IOException ex) {
            throw new InventoryException("Failed to read item from HBase", ex);
        }
    }

    public List<InventoryItem> findAll() {
        List<InventoryItem> items = new ArrayList<>();
        try (Table table = connection.getTable(TableName.valueOf(inventoryTable));
             ResultScanner scanner = table.getScanner(new Scan())) {
            for (Result result : scanner) {
                String itemId = Bytes.toString(result.getRow());
                items.add(resultToItem(itemId, result));
            }
            return items;
        } catch (IOException ex) {
            throw new InventoryException("Failed to scan items from HBase", ex);
        }
    }

    public void deleteById(String itemId) {
        try (Table table = connection.getTable(TableName.valueOf(inventoryTable))) {
            table.delete(new Delete(Bytes.toBytes(itemId)));
        } catch (IOException ex) {
            throw new InventoryException("Failed to delete item from HBase", ex);
        }
    }

    public void appendLog(String itemId, ChangeType changeType, Integer quantityChanged, Instant timestamp) {
        String rowKey = itemId + "_" + timestamp.toEpochMilli();
        try (Table table = connection.getTable(TableName.valueOf(logsTable))) {
            Put put = new Put(Bytes.toBytes(rowKey));
            put.addColumn(LOGS_CF, Bytes.toBytes("change_type"), Bytes.toBytes(changeType.name()));
            put.addColumn(LOGS_CF, Bytes.toBytes("quantity_changed"), Bytes.toBytes(quantityChanged));
            put.addColumn(LOGS_CF, Bytes.toBytes("timestamp"), Bytes.toBytes(timestamp.toString()));
            table.put(put);
        } catch (IOException ex) {
            throw new InventoryException("Failed to append inventory log", ex);
        }
    }

    private InventoryItem resultToItem(String itemId, Result result) {
        InventoryItem item = new InventoryItem();
        item.setItemId(itemId);
        item.setName(readString(result, DETAILS_CF, "name"));
        item.setCategory(readString(result, DETAILS_CF, "category"));
        item.setPrice(new BigDecimal(readString(result, DETAILS_CF, "price")));
        item.setStockQuantity(readInt(result, DETAILS_CF, "stock_quantity"));
        item.setLastUpdated(Instant.parse(readString(result, DETAILS_CF, "last_updated")));
        return item;
    }

    private String readString(Result result, byte[] cf, String qualifier) {
        byte[] value = result.getValue(cf, Bytes.toBytes(qualifier));
        return value == null ? "" : Bytes.toString(value);
    }

    private Integer readInt(Result result, byte[] cf, String qualifier) {
        byte[] value = result.getValue(cf, Bytes.toBytes(qualifier));
        if (value == null) {
            return 0;
        }
        return Bytes.toInt(value);
    }
}
