package com.orion.platform.analytics.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "order_analytics")
public class OrderAnalytics {
    
    @Id
    private String id;
    
    private String orderId;
    private String customerId;
    private Double totalAmount;
    private Integer itemCount;
    private Long timestamp;
    private Instant recordedAt;
    
    public OrderAnalytics() {
        this.recordedAt = Instant.now();
    }
    
    public OrderAnalytics(String orderId, String customerId, Double totalAmount, Integer itemCount, Long timestamp) {
        this();
        this.orderId = orderId;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.itemCount = itemCount;
        this.timestamp = timestamp;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public Double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public Integer getItemCount() {
        return itemCount;
    }
    
    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }
    
    public Long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    
    public Instant getRecordedAt() {
        return recordedAt;
    }
    
    public void setRecordedAt(Instant recordedAt) {
        this.recordedAt = recordedAt;
    }
}
