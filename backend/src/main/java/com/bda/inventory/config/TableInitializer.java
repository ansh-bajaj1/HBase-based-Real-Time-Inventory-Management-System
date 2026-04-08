package com.bda.inventory.config;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TableInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(TableInitializer.class);

    private final Connection connection;

    @Value("${inventory.hbase.inventory-table}")
    private String inventoryTable;

    @Value("${inventory.hbase.logs-table}")
    private String logsTable;

    public TableInitializer(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void run(String... args) throws Exception {
        try (Admin admin = connection.getAdmin()) {
            createTableIfMissing(admin, inventoryTable, "details");
            createTableIfMissing(admin, logsTable, "logs");
        }
    }

    private void createTableIfMissing(Admin admin, String tableName, String columnFamily) throws Exception {
        TableName tn = TableName.valueOf(tableName);
        if (admin.tableExists(tn)) {
            return;
        }

        TableDescriptor descriptor = TableDescriptorBuilder.newBuilder(tn)
                .setColumnFamily(ColumnFamilyDescriptorBuilder.of(columnFamily))
                .build();
        admin.createTable(descriptor);
        logger.info("Created HBase table: {}", tableName);
    }
}
