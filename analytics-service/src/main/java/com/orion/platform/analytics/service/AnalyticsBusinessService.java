package com.orion.platform.analytics.service;

import com.orion.platform.analytics.document.OrderAnalytics;
import com.orion.platform.analytics.repository.OrderAnalyticsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsBusinessService {
    
    private final OrderAnalyticsRepository analyticsRepository;
    
    public AnalyticsBusinessService(OrderAnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }
    
    public OrderAnalytics recordOrderEvent(String orderId, String customerId, Double totalAmount, 
                                          Integer itemCount, Long timestamp) {
        OrderAnalytics analytics = new OrderAnalytics(orderId, customerId, totalAmount, itemCount, timestamp);
        return analyticsRepository.save(analytics);
    }
    
    public OrderSummary getOrderSummary(Long startDate, Long endDate) {
        List<OrderAnalytics> orders = analyticsRepository.findByTimestampBetween(startDate, endDate);
        
        int totalOrders = orders.size();
        double totalRevenue = orders.stream()
            .mapToDouble(OrderAnalytics::getTotalAmount)
            .sum();
        double averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0.0;
        int totalItemsSold = orders.stream()
            .mapToInt(OrderAnalytics::getItemCount)
            .sum();
        
        return new OrderSummary(totalOrders, totalRevenue, averageOrderValue, totalItemsSold);
    }
    
    public static class OrderSummary {
        private final int totalOrders;
        private final double totalRevenue;
        private final double averageOrderValue;
        private final int totalItemsSold;
        
        public OrderSummary(int totalOrders, double totalRevenue, double averageOrderValue, int totalItemsSold) {
            this.totalOrders = totalOrders;
            this.totalRevenue = totalRevenue;
            this.averageOrderValue = averageOrderValue;
            this.totalItemsSold = totalItemsSold;
        }
        
        public int getTotalOrders() {
            return totalOrders;
        }
        
        public double getTotalRevenue() {
            return totalRevenue;
        }
        
        public double getAverageOrderValue() {
            return averageOrderValue;
        }
        
        public int getTotalItemsSold() {
            return totalItemsSold;
        }
    }
}
