package com.orion.platform.analytics.grpc;

import com.orion.platform.analytics.service.AnalyticsBusinessService;
import com.orion.platform.grpc.analytics.*;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AnalyticsGrpcService extends AnalyticsServiceGrpc.AnalyticsServiceImplBase {
    
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsGrpcService.class);
    
    private final AnalyticsBusinessService analyticsBusinessService;
    
    public AnalyticsGrpcService(AnalyticsBusinessService analyticsBusinessService) {
        this.analyticsBusinessService = analyticsBusinessService;
    }
    
    @Override
    public void recordOrderEvent(RecordOrderEventRequest request, StreamObserver<RecordOrderEventResponse> responseObserver) {
        try {
            logger.info("Recording order event for order: {}", request.getOrderId());
            
            analyticsBusinessService.recordOrderEvent(
                request.getOrderId(),
                request.getCustomerId(),
                request.getTotalAmount(),
                request.getItemCount(),
                request.getTimestamp()
            );
            
            RecordOrderEventResponse response = RecordOrderEventResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Order event recorded successfully")
                .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
            logger.info("Order event recorded: {}", request.getOrderId());
        } catch (Exception e) {
            logger.error("Error recording order event", e);
            
            RecordOrderEventResponse response = RecordOrderEventResponse.newBuilder()
                .setSuccess(false)
                .setMessage("Failed to record order event: " + e.getMessage())
                .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
    
    @Override
    public void getOrderSummary(GetOrderSummaryRequest request, StreamObserver<GetOrderSummaryResponse> responseObserver) {
        try {
            logger.info("Fetching order summary from {} to {}", request.getStartDate(), request.getEndDate());
            
            AnalyticsBusinessService.OrderSummary summary = analyticsBusinessService.getOrderSummary(
                request.getStartDate(),
                request.getEndDate()
            );
            
            GetOrderSummaryResponse response = GetOrderSummaryResponse.newBuilder()
                .setTotalOrders(summary.getTotalOrders())
                .setTotalRevenue(summary.getTotalRevenue())
                .setAverageOrderValue(summary.getAverageOrderValue())
                .setTotalItemsSold(summary.getTotalItemsSold())
                .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
            logger.info("Order summary fetched: {} orders, ${} revenue", 
                summary.getTotalOrders(), summary.getTotalRevenue());
        } catch (Exception e) {
            logger.error("Error fetching order summary", e);
            responseObserver.onError(e);
        }
    }
}
