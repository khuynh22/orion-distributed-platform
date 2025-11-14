package com.orion.platform.gateway.dto;

public class InventoryResponseDto {
    private String sku;
    private Integer quantity;
    private Long lastUpdated;
    
    public InventoryResponseDto() {}
    
    public InventoryResponseDto(String sku, Integer quantity, Long lastUpdated) {
        this.sku = sku;
        this.quantity = quantity;
        this.lastUpdated = lastUpdated;
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
    
    public Long getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
