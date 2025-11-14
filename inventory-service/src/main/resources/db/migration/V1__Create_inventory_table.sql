-- Create inventory table
CREATE TABLE IF NOT EXISTS inventory (
    sku VARCHAR(255) PRIMARY KEY,
    quantity INTEGER NOT NULL DEFAULT 0,
    last_updated TIMESTAMP NOT NULL
);

-- Create index
CREATE INDEX idx_inventory_quantity ON inventory(quantity);

-- Insert some sample data
INSERT INTO inventory (sku, quantity, last_updated) VALUES
    ('SKU-001', 100, CURRENT_TIMESTAMP),
    ('SKU-002', 200, CURRENT_TIMESTAMP),
    ('SKU-003', 150, CURRENT_TIMESTAMP),
    ('SKU-004', 75, CURRENT_TIMESTAMP),
    ('SKU-005', 300, CURRENT_TIMESTAMP);
