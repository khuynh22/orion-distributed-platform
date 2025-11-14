package com.orion.platform.inventory.grpc;

import com.orion.platform.grpc.inventory.*;
import com.orion.platform.inventory.entity.Inventory;
import com.orion.platform.inventory.service.InventoryBusinessService;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InventoryGrpcService extends InventoryServiceGrpc.InventoryServiceImplBase {
    
    private static final Logger logger = LoggerFactory.getLogger(InventoryGrpcService.class);
    
    private final InventoryBusinessService inventoryBusinessService;
    
    public InventoryGrpcService(InventoryBusinessService inventoryBusinessService) {
        this.inventoryBusinessService = inventoryBusinessService;
    }
    
    @Override
    public void adjustInventory(AdjustInventoryRequest request, StreamObserver<AdjustInventoryResponse> responseObserver) {
        try {
            logger.info("Adjusting inventory for SKU: {}, change: {}", request.getSku(), request.getQuantityChange());
            
            Inventory inventory = inventoryBusinessService.adjustInventory(
                request.getSku(),
                request.getQuantityChange(),
                request.getReason()
            );
            
            AdjustInventoryResponse response = AdjustInventoryResponse.newBuilder()
                .setSku(inventory.getSku())
                .setCurrentQuantity(inventory.getQuantity())
                .setSuccess(true)
                .setMessage("Inventory adjusted successfully")
                .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
            logger.info("Inventory adjusted for SKU: {}, new quantity: {}", inventory.getSku(), inventory.getQuantity());
        } catch (Exception e) {
            logger.error("Error adjusting inventory", e);
            
            AdjustInventoryResponse response = AdjustInventoryResponse.newBuilder()
                .setSku(request.getSku())
                .setSuccess(false)
                .setMessage("Failed to adjust inventory: " + e.getMessage())
                .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
    
    @Override
    public void getInventory(GetInventoryRequest request, StreamObserver<GetInventoryResponse> responseObserver) {
        try {
            logger.info("Fetching inventory for SKU: {}", request.getSku());
            
            Optional<Inventory> inventoryOpt = inventoryBusinessService.getInventory(request.getSku());
            
            if (inventoryOpt.isEmpty()) {
                responseObserver.onError(new RuntimeException("Inventory not found for SKU: " + request.getSku()));
                return;
            }
            
            Inventory inventory = inventoryOpt.get();
            
            GetInventoryResponse response = GetInventoryResponse.newBuilder()
                .setSku(inventory.getSku())
                .setQuantity(inventory.getQuantity())
                .setLastUpdated(inventory.getLastUpdated().toEpochMilli())
                .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
            logger.info("Inventory fetched for SKU: {}, quantity: {}", inventory.getSku(), inventory.getQuantity());
        } catch (Exception e) {
            logger.error("Error fetching inventory", e);
            responseObserver.onError(e);
        }
    }
}
