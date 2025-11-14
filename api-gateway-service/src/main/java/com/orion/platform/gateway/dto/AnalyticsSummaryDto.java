package com.orion.platform.gateway.dto;

public class AnalyticsSummaryDto {
    private Integer totalOrders;
    private Double totalRevenue;
    private Double averageOrderValue;
    private Integer totalItemsSold;
    
    public AnalyticsSummaryDto() {}
    
    public AnalyticsSummaryDto(Integer totalOrders, Double totalRevenue, Double averageOrderValue, Integer totalItemsSold) {
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
        this.averageOrderValue = averageOrderValue;
        this.totalItemsSold = totalItemsSold;
    }
    
    public Integer getTotalOrders() {
        return totalOrders;
    }
    
    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }
    
    public Double getTotalRevenue() {
        return totalRevenue;
    }
    
    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    
    public Double getAverageOrderValue() {
        return averageOrderValue;
    }
    
    public void setAverageOrderValue(Double averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }
    
    public Integer getTotalItemsSold() {
        return totalItemsSold;
    }
    
    public void setTotalItemsSold(Integer totalItemsSold) {
        this.totalItemsSold = totalItemsSold;
    }
}
