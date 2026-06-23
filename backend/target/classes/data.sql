-- Seed users
INSERT INTO users (first_name, last_name, email, phone_number, password_hash) VALUES ('Alice', 'Johnson', 'alice@example.com', '1234567890', 'hash1');
INSERT INTO users (first_name, last_name, email, phone_number, password_hash) VALUES ('Bob', 'Smith', 'bob@example.com', '0987654321', 'hash2');

-- Categories
INSERT INTO categories (name, parent_id) VALUES ('Tops', NULL);
INSERT INTO categories (name, parent_id) VALUES ('Shirts', 1);

-- Sizes
INSERT INTO sizes ( label, size_system) VALUES ('S', 'US');
INSERT INTO sizes (label, size_system) VALUES ('M', 'US');

-- Colors
INSERT INTO colors (name, color_value, image_url) VALUES ('Red', '#ff0000', NULL);
INSERT INTO colors (name, color_value, image_url) VALUES ('Blue', '#0000ff', NULL);

-- Items
INSERT INTO items (name, price, description, audience, category_id) VALUES ('Casual Shirt', 29.99, 'A comfortable casual shirt.', 'UNISEX', 2);
INSERT INTO items (name, price, description, audience, category_id) VALUES ('T-Shirt', 19.99, 'Basic cotton t-shirt.', 'MEN', 1);

-- Item variants
INSERT INTO item_variants (is_active, stock_left, item_id, size_id, color_id) VALUES (TRUE, 10, 1, 2, 1);
INSERT INTO item_variants (is_active, stock_left, item_id, size_id, color_id) VALUES (TRUE, 5, 2, 1, 2);

-- Addresses
INSERT INTO addresses (country, region, city, postal_code, address_line, user_id) VALUES ( 'USA', 'California', 'San Francisco', 94105, '123 Market St', 1);

-- Favorites
INSERT INTO favourites (item_variant_id, user_id) VALUES (1, 1);

-- Cart items
INSERT INTO cart_items (quantity, item_variant_id, user_id) VALUES (2, 1, 2);

-- Reviews
INSERT INTO reviews (body, size_fit, quality, comfort, user_id, item_variant_id) VALUES ('Great fit and comfortable.', 'TRUE_TO_SIZE', 'EXCELLENT', 'VERY_COMFORTABLE', 1, 1);


