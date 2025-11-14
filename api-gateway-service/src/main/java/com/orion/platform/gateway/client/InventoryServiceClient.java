package com.orion.platform.gateway.client;

import com.orion.platform.grpc.inventory.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class InventoryServiceClient {
    
    @Value("${grpc.inventory.host:localhost}")
    private String host;
    
    @Value("${grpc.inventory.port:50052}")
    private int port;
    
    private ManagedChannel channel;
    private InventoryServiceGrpc.InventoryServiceBlockingStub stub;
    
    @PostConstruct
    public void init() {
        channel = ManagedChannelBuilder
            .forAddress(host, port)
            .usePlaintext()
            .build();
        stub = InventoryServiceGrpc.newBlockingStub(channel);
    }
    
    @PreDestroy
    public void cleanup() {
        if (channel != null) {
            channel.shutdown();
        }
    }
    
    public GetInventoryResponse getInventory(String sku) {
        return stub.getInventory(GetInventoryRequest.newBuilder().setSku(sku).build());
    }
}
