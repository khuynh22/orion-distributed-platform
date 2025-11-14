package com.orion.platform.inventory.service;

import com.orion.platform.inventory.entity.Inventory;
import com.orion.platform.inventory.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class InventoryBusinessService {
    
    private final InventoryRepository inventoryRepository;
    
    public InventoryBusinessService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }
    
    @Transactional
    public Inventory adjustInventory(String sku, int quantityChange, String reason) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findById(sku);
        
        Inventory inventory;
        if (inventoryOpt.isPresent()) {
            inventory = inventoryOpt.get();
            inventory.adjustQuantity(quantityChange);
        } else {
            // Create new inventory entry if it doesn't exist
            inventory = new Inventory(sku, Math.max(0, quantityChange));
        }
        
        return inventoryRepository.save(inventory);
    }
    
    public Optional<Inventory> getInventory(String sku) {
        return inventoryRepository.findById(sku);
    }
}
