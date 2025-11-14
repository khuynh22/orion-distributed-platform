package com.orion.platform.order.service;

import com.orion.platform.grpc.order.CreateOrderRequest;
import com.orion.platform.grpc.order.OrderItemRequest;
import com.orion.platform.order.entity.Order;
import com.orion.platform.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderBusinessServiceTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    @InjectMocks
    private OrderBusinessService orderBusinessService;
    
    @Test
    void testCreateOrder() {
        // Arrange
        CreateOrderRequest request = CreateOrderRequest.newBuilder()
            .setCustomerId("customer-123")
            .addItems(OrderItemRequest.newBuilder()
                .setSku("SKU-001")
                .setQuantity(2)
                .setPrice(29.99)
                .build())
            .build();
        
        Order savedOrder = new Order("customer-123", 59.98);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        
        // Act
        Order result = orderBusinessService.createOrder(request);
        
        // Assert
        assertNotNull(result);
        assertEquals("customer-123", result.getCustomerId());
        assertEquals(59.98, result.getTotalAmount());
        verify(orderRepository, times(1)).save(any(Order.class));
    }
    
    @Test
    void testGetOrder() {
        // Arrange
        String orderId = "test-order-id";
        Order order = new Order("customer-123", 59.98);
        order.setOrderId(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        
        // Act
        Optional<Order> result = orderBusinessService.getOrder(orderId);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(orderId, result.get().getOrderId());
        verify(orderRepository, times(1)).findById(orderId);
    }
    
    @Test
    void testGetOrderNotFound() {
        // Arrange
        String orderId = "non-existent-id";
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        
        // Act
        Optional<Order> result = orderBusinessService.getOrder(orderId);
        
        // Assert
        assertFalse(result.isPresent());
        verify(orderRepository, times(1)).findById(orderId);
    }
}
