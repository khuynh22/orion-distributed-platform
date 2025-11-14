package com.orion.platform.gateway.client;

import com.orion.platform.grpc.analytics.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class AnalyticsServiceClient {
    
    @Value("${grpc.analytics.host:localhost}")
    private String host;
    
    @Value("${grpc.analytics.port:50053}")
    private int port;
    
    private ManagedChannel channel;
    private AnalyticsServiceGrpc.AnalyticsServiceBlockingStub stub;
    
    @PostConstruct
    public void init() {
        channel = ManagedChannelBuilder
            .forAddress(host, port)
            .usePlaintext()
            .build();
        stub = AnalyticsServiceGrpc.newBlockingStub(channel);
    }
    
    @PreDestroy
    public void cleanup() {
        if (channel != null) {
            channel.shutdown();
        }
    }
    
    public GetOrderSummaryResponse getOrderSummary(Long startDate, Long endDate) {
        return stub.getOrderSummary(
            GetOrderSummaryRequest.newBuilder()
                .setStartDate(startDate)
                .setEndDate(endDate)
                .build()
        );
    }
}
