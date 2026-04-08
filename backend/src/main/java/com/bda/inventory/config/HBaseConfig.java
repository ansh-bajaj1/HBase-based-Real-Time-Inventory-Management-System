package com.bda.inventory.config;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class HBaseConfig {

    @Value("${inventory.hbase.zookeeper-quorum}")
    private String zookeeperQuorum;

    @Value("${inventory.hbase.zookeeper-port}")
    private String zookeeperPort;

    @Bean
    public Configuration hbaseConfiguration() {
        Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", zookeeperQuorum);
        configuration.set("hbase.zookeeper.property.clientPort", zookeeperPort);
        configuration.set("hbase.client.retries.number", "5");
        configuration.set("hbase.rpc.timeout", "20000");
        return configuration;
    }

    @Bean(destroyMethod = "close")
    public Connection hbaseConnection(Configuration hbaseConfiguration) throws IOException {
        return ConnectionFactory.createConnection(hbaseConfiguration);
    }
}
