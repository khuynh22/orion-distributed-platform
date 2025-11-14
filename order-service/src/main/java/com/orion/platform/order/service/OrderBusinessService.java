package com.orion.platform.order.service;

import com.orion.platform.grpc.analytics.AnalyticsServiceGrpc;
import com.orion.platform.grpc.analytics.RecordOrderEventRequest;
import com.orion.platform.grpc.analytics.RecordOrderEventResponse;
import com.orion.platform.grpc.inventory.AdjustInventoryRequest;
import com.orion.platform.grpc.inventory.AdjustInventoryResponse;
import com.orion.platform.grpc.inventory.InventoryServiceGrpc;
import com.orion.platform.grpc.order.CreateOrderRequest;
import com.orion.platform.grpc.order.OrderItemRequest;
import com.orion.platform.order.entity.Order;
import com.orion.platform.order.entity.OrderItem;
import com.orion.platform.order.repository.OrderRepository;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Optional;

@Service
public class OrderBusinessService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderBusinessService.class);
    
    private final OrderRepository orderRepository;
    
    @Value("${grpc.inventory.host:localhost}")
    private String inventoryHost;
    
    @Value("${grpc.inventory.port:50052}")
    private int inventoryPort;
    
    @Value("${grpc.analytics.host:localhost}")
    private String analyticsHost;
    
    @Value("${grpc.analytics.port:50053}")
    private int analyticsPort;
    
    private ManagedChannel inventoryChannel;
    private ManagedChannel analyticsChannel;
    private InventoryServiceGrpc.InventoryServiceBlockingStub inventoryStub;
    private AnalyticsServiceGrpc.AnalyticsServiceBlockingStub analyticsStub;
    
    public OrderBusinessService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    @PostConstruct
    public void init() {
        inventoryChannel = ManagedChannelBuilder
            .forAddress(inventoryHost, inventoryPort)
            .usePlaintext()
            .build();
        inventoryStub = InventoryServiceGrpc.newBlockingStub(inventoryChannel);
        
        analyticsChannel = ManagedChannelBuilder
            .forAddress(analyticsHost, analyticsPort)
            .usePlaintext()
            .build();
        analyticsStub = AnalyticsServiceGrpc.newBlockingStub(analyticsChannel);
    }
    
    @PreDestroy
    public void cleanup() {
        if (inventoryChannel != null) {
            inventoryChannel.shutdown();
        }
        if (analyticsChannel != null) {
            analyticsChannel.shutdown();
        }
    }
    
    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        // Calculate total
        double totalAmount = request.getItemsList().stream()
            .mapToDouble(item -> item.getPrice() * item.getQuantity())
            .sum();
        
        // Create order entity
        Order order = new Order(request.getCustomerId(), totalAmount);
        
        // Add items
        for (OrderItemRequest itemReq : request.getItemsList()) {
            OrderItem item = new OrderItem(itemReq.getSku(), itemReq.getQuantity(), itemReq.getPrice());
            order.addItem(item);
            
            // Adjust inventory
            try {
                AdjustInventoryResponse inventoryResponse = inventoryStub.adjustInventory(
                    AdjustInventoryRequest.newBuilder()
                        .setSku(itemReq.getSku())
                        .setQuantityChange(-itemReq.getQuantity())
                        .setReason("Order: " + order.getOrderId())
                        .build()
                );
                
                if (!inventoryResponse.getSuccess()) {
                    logger.warn("Inventory adjustment failed for SKU {}: {}", 
                        itemReq.getSku(), inventoryResponse.getMessage());
                }
            } catch (Exception e) {
                logger.error("Failed to adjust inventory for SKU {}", itemReq.getSku(), e);
            }
        }
        
        // Save order
        Order savedOrder = orderRepository.save(order);
        
        // Record analytics event
        try {
            RecordOrderEventResponse analyticsResponse = analyticsStub.recordOrderEvent(
                RecordOrderEventRequest.newBuilder()
                    .setOrderId(savedOrder.getOrderId())
                    .setCustomerId(savedOrder.getCustomerId())
                    .setTotalAmount(savedOrder.getTotalAmount())
                    .setItemCount(savedOrder.getItems().size())
                    .setTimestamp(savedOrder.getCreatedAt().toEpochMilli())
                    .build()
            );
            
            if (!analyticsResponse.getSuccess()) {
                logger.warn("Failed to record analytics: {}", analyticsResponse.getMessage());
            }
        } catch (Exception e) {
            logger.error("Failed to record order analytics", e);
        }
        
        return savedOrder;
    }
    
    public Optional<Order> getOrder(String orderId) {
        return orderRepository.findById(orderId);
    }
}
