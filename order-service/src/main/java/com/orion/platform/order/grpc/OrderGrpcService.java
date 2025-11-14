package com.orion.platform.order.grpc;

import com.orion.platform.grpc.order.*;
import com.orion.platform.order.entity.Order;
import com.orion.platform.order.entity.OrderItem;
import com.orion.platform.order.service.OrderBusinessService;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderGrpcService extends OrderServiceGrpc.OrderServiceImplBase {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderGrpcService.class);
    
    private final OrderBusinessService orderBusinessService;
    
    public OrderGrpcService(OrderBusinessService orderBusinessService) {
        this.orderBusinessService = orderBusinessService;
    }
    
    @Override
    public void createOrder(CreateOrderRequest request, StreamObserver<CreateOrderResponse> responseObserver) {
        try {
            logger.info("Creating order for customer: {}", request.getCustomerId());
            
            Order order = orderBusinessService.createOrder(request);
            
            CreateOrderResponse response = CreateOrderResponse.newBuilder()
                .setOrderId(order.getOrderId())
                .setStatus(order.getStatus())
                .setTotalAmount(order.getTotalAmount())
                .setCreatedAt(order.getCreatedAt().toEpochMilli())
                .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
            logger.info("Order created successfully: {}", order.getOrderId());
        } catch (Exception e) {
            logger.error("Error creating order", e);
            responseObserver.onError(e);
        }
    }
    
    @Override
    public void getOrder(GetOrderRequest request, StreamObserver<GetOrderResponse> responseObserver) {
        try {
            logger.info("Fetching order: {}", request.getOrderId());
            
            Optional<Order> orderOpt = orderBusinessService.getOrder(request.getOrderId());
            
            if (orderOpt.isEmpty()) {
                responseObserver.onError(new RuntimeException("Order not found: " + request.getOrderId()));
                return;
            }
            
            Order order = orderOpt.get();
            
            GetOrderResponse.Builder responseBuilder = GetOrderResponse.newBuilder()
                .setOrderId(order.getOrderId())
                .setCustomerId(order.getCustomerId())
                .setStatus(order.getStatus())
                .setTotalAmount(order.getTotalAmount())
                .setCreatedAt(order.getCreatedAt().toEpochMilli());
            
            for (OrderItem item : order.getItems()) {
                responseBuilder.addItems(
                    com.orion.platform.grpc.order.OrderItem.newBuilder()
                        .setSku(item.getSku())
                        .setQuantity(item.getQuantity())
                        .setPrice(item.getPrice())
                        .build()
                );
            }
            
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
            
            logger.info("Order fetched successfully: {}", order.getOrderId());
        } catch (Exception e) {
            logger.error("Error fetching order", e);
            responseObserver.onError(e);
        }
    }
}
