package com.orion.platform.gateway.client;

import com.orion.platform.grpc.order.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class OrderServiceClient {
    
    @Value("${grpc.order.host:localhost}")
    private String host;
    
    @Value("${grpc.order.port:50051}")
    private int port;
    
    private ManagedChannel channel;
    private OrderServiceGrpc.OrderServiceBlockingStub stub;
    
    @PostConstruct
    public void init() {
        channel = ManagedChannelBuilder
            .forAddress(host, port)
            .usePlaintext()
            .build();
        stub = OrderServiceGrpc.newBlockingStub(channel);
    }
    
    @PreDestroy
    public void cleanup() {
        if (channel != null) {
            channel.shutdown();
        }
    }
    
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        return stub.createOrder(request);
    }
    
    public GetOrderResponse getOrder(String orderId) {
        return stub.getOrder(GetOrderRequest.newBuilder().setOrderId(orderId).build());
    }
}
