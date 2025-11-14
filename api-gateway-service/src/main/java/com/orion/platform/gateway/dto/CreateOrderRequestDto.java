package com.orion.platform.gateway.dto;

import java.util.List;

public class CreateOrderRequestDto {
    private String customerId;
    private List<OrderItemDto> items;
    
    public CreateOrderRequestDto() {}
    
    public CreateOrderRequestDto(String customerId, List<OrderItemDto> items) {
        this.customerId = customerId;
        this.items = items;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public List<OrderItemDto> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItemDto> items) {
        this.items = items;
    }
    
    public static class OrderItemDto {
        private String sku;
        private Integer quantity;
        private Double price;
        
        public OrderItemDto() {}
        
        public OrderItemDto(String sku, Integer quantity, Double price) {
            this.sku = sku;
            this.quantity = quantity;
            this.price = price;
        }
        
        public String getSku() {
            return sku;
        }
        
        public void setSku(String sku) {
            this.sku = sku;
        }
        
        public Integer getQuantity() {
            return quantity;
        }
        
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
        
        public Double getPrice() {
            return price;
        }
        
        public void setPrice(Double price) {
            this.price = price;
        }
    }
}
