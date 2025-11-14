package com.orion.platform.inventory.service;

import com.orion.platform.inventory.entity.Inventory;
import com.orion.platform.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryBusinessServiceTest {
    
    @Mock
    private InventoryRepository inventoryRepository;
    
    @InjectMocks
    private InventoryBusinessService inventoryBusinessService;
    
    @Test
    void testAdjustInventoryExistingSku() {
        // Arrange
        String sku = "SKU-001";
        Inventory existingInventory = new Inventory(sku, 100);
        when(inventoryRepository.findById(sku)).thenReturn(Optional.of(existingInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));
        
        // Act
        Inventory result = inventoryBusinessService.adjustInventory(sku, -10, "Order");
        
        // Assert
        assertNotNull(result);
        assertEquals(sku, result.getSku());
        assertEquals(90, result.getQuantity());
        verify(inventoryRepository, times(1)).findById(sku);
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }
    
    @Test
    void testAdjustInventoryNewSku() {
        // Arrange
        String sku = "SKU-NEW";
        when(inventoryRepository.findById(sku)).thenReturn(Optional.empty());
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));
        
        // Act
        Inventory result = inventoryBusinessService.adjustInventory(sku, 50, "Initial stock");
        
        // Assert
        assertNotNull(result);
        assertEquals(sku, result.getSku());
        assertEquals(50, result.getQuantity());
        verify(inventoryRepository, times(1)).findById(sku);
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }
    
    @Test
    void testGetInventory() {
        // Arrange
        String sku = "SKU-001";
        Inventory inventory = new Inventory(sku, 100);
        when(inventoryRepository.findById(sku)).thenReturn(Optional.of(inventory));
        
        // Act
        Optional<Inventory> result = inventoryBusinessService.getInventory(sku);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(sku, result.get().getSku());
        assertEquals(100, result.get().getQuantity());
        verify(inventoryRepository, times(1)).findById(sku);
    }
}
