package com.orion.platform.analytics.service;

import com.orion.platform.analytics.document.OrderAnalytics;
import com.orion.platform.analytics.repository.OrderAnalyticsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsBusinessServiceTest {
    
    @Mock
    private OrderAnalyticsRepository analyticsRepository;
    
    @InjectMocks
    private AnalyticsBusinessService analyticsBusinessService;
    
    @Test
    void testRecordOrderEvent() {
        // Arrange
        OrderAnalytics analytics = new OrderAnalytics("order-123", "customer-123", 100.0, 2, 123456789L);
        when(analyticsRepository.save(any(OrderAnalytics.class))).thenReturn(analytics);
        
        // Act
        OrderAnalytics result = analyticsBusinessService.recordOrderEvent(
            "order-123", "customer-123", 100.0, 2, 123456789L
        );
        
        // Assert
        assertNotNull(result);
        assertEquals("order-123", result.getOrderId());
        assertEquals(100.0, result.getTotalAmount());
        verify(analyticsRepository, times(1)).save(any(OrderAnalytics.class));
    }
    
    @Test
    void testGetOrderSummary() {
        // Arrange
        List<OrderAnalytics> orders = Arrays.asList(
            new OrderAnalytics("order-1", "customer-1", 100.0, 2, 123456789L),
            new OrderAnalytics("order-2", "customer-2", 200.0, 3, 123456790L),
            new OrderAnalytics("order-3", "customer-3", 150.0, 1, 123456791L)
        );
        when(analyticsRepository.findByTimestampBetween(anyLong(), anyLong())).thenReturn(orders);
        
        // Act
        AnalyticsBusinessService.OrderSummary summary = analyticsBusinessService.getOrderSummary(0L, 999999999999L);
        
        // Assert
        assertNotNull(summary);
        assertEquals(3, summary.getTotalOrders());
        assertEquals(450.0, summary.getTotalRevenue());
        assertEquals(150.0, summary.getAverageOrderValue());
        assertEquals(6, summary.getTotalItemsSold());
        verify(analyticsRepository, times(1)).findByTimestampBetween(anyLong(), anyLong());
    }
    
    @Test
    void testGetOrderSummaryEmpty() {
        // Arrange
        when(analyticsRepository.findByTimestampBetween(anyLong(), anyLong())).thenReturn(Arrays.asList());
        
        // Act
        AnalyticsBusinessService.OrderSummary summary = analyticsBusinessService.getOrderSummary(0L, 999999999999L);
        
        // Assert
        assertNotNull(summary);
        assertEquals(0, summary.getTotalOrders());
        assertEquals(0.0, summary.getTotalRevenue());
        assertEquals(0.0, summary.getAverageOrderValue());
        assertEquals(0, summary.getTotalItemsSold());
    }
}
