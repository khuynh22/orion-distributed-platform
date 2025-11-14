package com.orion.platform.inventory.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "inventory")
public class Inventory {
    
    @Id
    private String sku;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false)
    private Instant lastUpdated;
    
    public Inventory() {
        this.lastUpdated = Instant.now();
    }
    
    public Inventory(String sku, Integer quantity) {
        this();
        this.sku = sku;
        this.quantity = quantity;
    }
    
    public void adjustQuantity(int change) {
        this.quantity += change;
        this.lastUpdated = Instant.now();
    }
    
    // Getters and Setters
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
    
    public Instant getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
