-- Seed users
INSERT INTO users (id, first_name, last_name, email, phone_number, password_hash) VALUES (1, 'Alice', 'Johnson', 'alice@example.com', '1234567890', 'hash1');
INSERT INTO users (id, first_name, last_name, email, phone_number, password_hash) VALUES (2, 'Bob', 'Smith', 'bob@example.com', '0987654321', 'hash2');

-- Categories
INSERT INTO categories (id, name, parent_id) VALUES (1, 'Tops', NULL);
INSERT INTO categories (id, name, parent_id) VALUES (2, 'Shirts', 1);

-- Sizes
INSERT INTO sizes (id, label, size_system) VALUES (1, 'S', 'US');
INSERT INTO sizes (id, label, size_system) VALUES (2, 'M', 'US');

-- Colors
INSERT INTO colors (id, name, color_value, image_url) VALUES (1, 'Red', '#ff0000', NULL);
INSERT INTO colors (id, name, color_value, image_url) VALUES (2, 'Blue', '#0000ff', NULL);

-- Items
INSERT INTO items (id, name, price, description, audience, category_id) VALUES (1, 'Casual Shirt', 29.99, 'A comfortable casual shirt.', 'UNISEX', 2);
INSERT INTO items (id, name, price, description, audience, category_id) VALUES (2, 'T-Shirt', 19.99, 'Basic cotton t-shirt.', 'MEN', 1);

-- Item variants
INSERT INTO item_variants (id, is_active, stock_left, item_id, size_id, color_id) VALUES (1, TRUE, 10, 1, 2, 1);
INSERT INTO item_variants (id, is_active, stock_left, item_id, size_id, color_id) VALUES (2, TRUE, 5, 2, 1, 2);

-- Addresses
INSERT INTO addresses (id, country, region, city, postal_code, address_line, user_id) VALUES (1, 'USA', 'California', 'San Francisco', 94105, '123 Market St', 1);

-- Favorites
INSERT INTO favourites (id, item_variant_id, user_id) VALUES (1, 1, 1);

-- Cart items
INSERT INTO cart_items (id, quantity, item_variant_id, user_id) VALUES (1, 2, 1, 2);

-- Reviews
INSERT INTO reviews (id, body, size_fit, quality, comfort, user_id, item_variant_id) VALUES (1, 'Great fit and comfortable.', 'TRUE_TO_SIZE', 'EXCELLENT', 'VERY_COMFORTABLE', 1, 1);


