package com.orion.platform.inventory.config;

import com.orion.platform.inventory.grpc.InventoryGrpcService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;

@Configuration
public class GrpcServerConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(GrpcServerConfig.class);
    
    @Value("${grpc.server.port:50052}")
    private int port;
    
    private Server server;
    private final InventoryGrpcService inventoryGrpcService;
    
    public GrpcServerConfig(InventoryGrpcService inventoryGrpcService) {
        this.inventoryGrpcService = inventoryGrpcService;
    }
    
    @PostConstruct
    public void start() throws IOException {
        server = ServerBuilder
            .forPort(port)
            .addService(inventoryGrpcService)
            .build()
            .start();
        
        logger.info("gRPC Server started on port {}", port);
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gRPC server");
            GrpcServerConfig.this.stop();
        }));
    }
    
    @PreDestroy
    public void stop() {
        if (server != null) {
            server.shutdown();
            logger.info("gRPC Server stopped");
        }
    }
    
    @Bean
    public Server grpcServer() {
        return server;
    }
}
