package com.orion.platform.gateway.controller;

import com.orion.platform.gateway.client.AnalyticsServiceClient;
import com.orion.platform.gateway.client.InventoryServiceClient;
import com.orion.platform.gateway.client.OrderServiceClient;
import com.orion.platform.gateway.dto.*;
import com.orion.platform.grpc.analytics.GetOrderSummaryResponse;
import com.orion.platform.grpc.inventory.GetInventoryResponse;
import com.orion.platform.grpc.order.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@Tag(name = "Orion API Gateway", description = "REST API for Orion Distributed Platform")
public class GatewayController {
    
    private final OrderServiceClient orderServiceClient;
    private final InventoryServiceClient inventoryServiceClient;
    private final AnalyticsServiceClient analyticsServiceClient;
    
    public GatewayController(OrderServiceClient orderServiceClient,
                            InventoryServiceClient inventoryServiceClient,
                            AnalyticsServiceClient analyticsServiceClient) {
        this.orderServiceClient = orderServiceClient;
        this.inventoryServiceClient = inventoryServiceClient;
        this.analyticsServiceClient = analyticsServiceClient;
    }
    
    @PostMapping("/orders")
    @Operation(summary = "Create a new order")
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody CreateOrderRequestDto request) {
        try {
            CreateOrderRequest.Builder grpcRequest = CreateOrderRequest.newBuilder()
                .setCustomerId(request.getCustomerId());
            
            for (CreateOrderRequestDto.OrderItemDto item : request.getItems()) {
                grpcRequest.addItems(
                    OrderItemRequest.newBuilder()
                        .setSku(item.getSku())
                        .setQuantity(item.getQuantity())
                        .setPrice(item.getPrice())
                        .build()
                );
            }
            
            CreateOrderResponse grpcResponse = orderServiceClient.createOrder(grpcRequest.build());
            
            OrderResponseDto response = new OrderResponseDto();
            response.setOrderId(grpcResponse.getOrderId());
            response.setStatus(grpcResponse.getStatus());
            response.setTotalAmount(grpcResponse.getTotalAmount());
            response.setCreatedAt(grpcResponse.getCreatedAt());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/orders/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderResponseDto> getOrder(@PathVariable String id) {
        try {
            GetOrderResponse grpcResponse = orderServiceClient.getOrder(id);
            
            OrderResponseDto response = new OrderResponseDto();
            response.setOrderId(grpcResponse.getOrderId());
            response.setCustomerId(grpcResponse.getCustomerId());
            response.setStatus(grpcResponse.getStatus());
            response.setTotalAmount(grpcResponse.getTotalAmount());
            response.setCreatedAt(grpcResponse.getCreatedAt());
            response.setItems(
                grpcResponse.getItemsList().stream()
                    .map(item -> new OrderResponseDto.OrderItemDto(
                        item.getSku(),
                        item.getQuantity(),
                        item.getPrice()
                    ))
                    .collect(Collectors.toList())
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/inventory/{sku}")
    @Operation(summary = "Get inventory for SKU")
    public ResponseEntity<InventoryResponseDto> getInventory(@PathVariable String sku) {
        try {
            GetInventoryResponse grpcResponse = inventoryServiceClient.getInventory(sku);
            
            InventoryResponseDto response = new InventoryResponseDto(
                grpcResponse.getSku(),
                grpcResponse.getQuantity(),
                grpcResponse.getLastUpdated()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/analytics/summary")
    @Operation(summary = "Get analytics summary")
    public ResponseEntity<AnalyticsSummaryDto> getAnalyticsSummary(
            @RequestParam(required = false) Long startDate,
            @RequestParam(required = false) Long endDate) {
        try {
            // Default to last 30 days if not specified
            if (startDate == null) {
                startDate = Instant.now().minus(30, ChronoUnit.DAYS).toEpochMilli();
            }
            if (endDate == null) {
                endDate = Instant.now().toEpochMilli();
            }
            
            GetOrderSummaryResponse grpcResponse = analyticsServiceClient.getOrderSummary(startDate, endDate);
            
            AnalyticsSummaryDto response = new AnalyticsSummaryDto(
                grpcResponse.getTotalOrders(),
                grpcResponse.getTotalRevenue(),
                grpcResponse.getAverageOrderValue(),
                grpcResponse.getTotalItemsSold()
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
