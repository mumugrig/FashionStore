-- Seed users. Password for every seeded account is "password".
INSERT INTO users (first_name, last_name, email, phone_number, password_hash, role) VALUES
('Store', 'Admin', 'admin@fashionstore.com', '+359888000001', '$2a$10$xUgk8N.u8Q1bz6RrxnPXEukfTEyhpE9m4ZZZFmf80QdhaBZxzIsoG', 'ADMIN'),
('Alice', 'Johnson', 'alice@example.com', '+359888000002', '$2a$10$xUgk8N.u8Q1bz6RrxnPXEukfTEyhpE9m4ZZZFmf80QdhaBZxzIsoG', 'ADMIN'),
('Bob', 'Smith', 'bob@example.com', '+359888000003', '$2a$10$xUgk8N.u8Q1bz6RrxnPXEukfTEyhpE9m4ZZZFmf80QdhaBZxzIsoG', 'USER'),
('Maya', 'Petrova', 'maya.petrova@example.com', '+359888000004', '$2a$10$xUgk8N.u8Q1bz6RrxnPXEukfTEyhpE9m4ZZZFmf80QdhaBZxzIsoG', 'USER'),
('Nikolay', 'Dimitrov', 'nikolay.dimitrov@example.com', '+359888000005', '$2a$10$xUgk8N.u8Q1bz6RrxnPXEukfTEyhpE9m4ZZZFmf80QdhaBZxzIsoG', 'USER'),
('Elena', 'Ivanova', 'elena.ivanova@example.com', '+359888000006', '$2a$10$xUgk8N.u8Q1bz6RrxnPXEukfTEyhpE9m4ZZZFmf80QdhaBZxzIsoG', 'USER');

-- Categories
INSERT INTO categories (name, parent_id) VALUES
('Women', NULL),
('Men', NULL),
('Kids', NULL),
('Tops', NULL),
('Outerwear', NULL),
('Bottoms', NULL),
('Shoes', NULL),
('Accessories', NULL),
('Shirts', 4),
('T-Shirts', 4),
('Knitwear', 4),
('Jackets', 5),
('Coats', 5),
('Jeans', 6),
('Trousers', 6),
('Dresses', 1),
('Sneakers', 7),
('Bags', 8);

-- Sizes
INSERT INTO sizes (label, size_system) VALUES
('XS', 'ALPHA'),
('S', 'ALPHA'),
('M', 'ALPHA'),
('L', 'ALPHA'),
('XL', 'ALPHA'),
('38', 'EU'),
('40', 'EU'),
('42', 'EU'),
('44', 'EU'),
('46', 'EU'),
('8', 'US'),
('9', 'US');

-- Colors. image_url is a legacy fallback image used when item and variant images are missing.
INSERT INTO colors (name, color_value, image_url) VALUES
('White', '#f8f7f3', 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=80'),
('Black', '#111111', 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=900&q=80'),
('Navy', '#1f2a44', 'https://images.unsplash.com/photo-1523398002811-999ca8dec234?auto=format&fit=crop&w=900&q=80'),
('Ecru', '#ded6c8', 'https://images.unsplash.com/photo-1543076447-215ad9ba6923?auto=format&fit=crop&w=900&q=80'),
('Camel', '#b08a61', 'https://images.unsplash.com/photo-1542272604-787c3835535d?auto=format&fit=crop&w=900&q=80'),
('Washed Blue', '#7d9fbd', 'https://images.unsplash.com/photo-1541099649105-f69ad21f3246?auto=format&fit=crop&w=900&q=80'),
('Burgundy', '#6f1d2d', 'https://images.unsplash.com/photo-1496747611176-843222e1e57c?auto=format&fit=crop&w=900&q=80'),
('Olive', '#59613a', 'https://images.unsplash.com/photo-1551028719-00167b16eac5?auto=format&fit=crop&w=900&q=80');

-- Items
INSERT INTO items (name, price, description, image_url, audience, category_id) VALUES
('Oxford Shirt', 49.90, 'Regular fit cotton oxford shirt with buttoned cuffs and a crisp everyday collar.', 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=80', 'MEN', 9),
('Linen Blend Shirt', 59.90, 'Lightweight linen blend shirt with a relaxed silhouette for warm weather styling.', 'https://images.unsplash.com/photo-1523398002811-999ca8dec234?auto=format&fit=crop&w=900&q=80', 'UNISEX', 9),
('Basic Heavyweight T-Shirt', 19.90, 'Dense cotton jersey t-shirt with a straight cut and soft hand feel.', 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=80', 'UNISEX', 10),
('Ribbed Knit Top', 29.90, 'Slim ribbed knit top with stretch comfort and a clean neckline.', 'https://images.unsplash.com/photo-1496747611176-843222e1e57c?auto=format&fit=crop&w=900&q=80', 'WOMEN', 11),
('Textured Knit Sweater', 69.90, 'Textured mid-weight sweater designed for layering through colder seasons.', 'https://images.unsplash.com/photo-1543076447-215ad9ba6923?auto=format&fit=crop&w=900&q=80', 'UNISEX', 11),
('Tailored Blazer', 129.90, 'Single-breasted tailored blazer with structured shoulders and flap pockets.', 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=900&q=80', 'WOMEN', 12),
('Denim Jacket', 79.90, 'Classic washed denim jacket with metal buttons and front patch pockets.', 'https://images.unsplash.com/photo-1541099649105-f69ad21f3246?auto=format&fit=crop&w=900&q=80', 'UNISEX', 12),
('Wool Blend Coat', 179.90, 'Long wool blend coat with a lapel collar and hidden front fastening.', 'https://images.unsplash.com/photo-1542272604-787c3835535d?auto=format&fit=crop&w=900&q=80', 'WOMEN', 13),
('Puffer Jacket', 149.90, 'Warm quilted puffer jacket with high collar, zip fastening, and side pockets.', 'https://images.unsplash.com/photo-1551028719-00167b16eac5?auto=format&fit=crop&w=900&q=80', 'MEN', 12),
('Straight Fit Jeans', 59.90, 'Five-pocket straight fit jeans in rigid cotton denim.', 'https://images.unsplash.com/photo-1541099649105-f69ad21f3246?auto=format&fit=crop&w=900&q=80', 'UNISEX', 14),
('Wide Leg Trousers', 69.90, 'Fluid wide leg trousers with a high waist and pressed front crease.', 'https://images.unsplash.com/photo-1542272604-787c3835535d?auto=format&fit=crop&w=900&q=80', 'WOMEN', 15),
('Pleated Chinos', 64.90, 'Cotton twill chinos with front pleats and a tapered leg.', 'https://images.unsplash.com/photo-1542272604-787c3835535d?auto=format&fit=crop&w=900&q=80', 'MEN', 15),
('Satin Midi Dress', 89.90, 'Satin midi dress with a fluid drape, side slit, and adjustable straps.', 'https://images.unsplash.com/photo-1496747611176-843222e1e57c?auto=format&fit=crop&w=900&q=80', 'WOMEN', 16),
('Poplin Shirt Dress', 79.90, 'Cotton poplin shirt dress with a belt and curved hem.', 'https://images.unsplash.com/photo-1523398002811-999ca8dec234?auto=format&fit=crop&w=900&q=80', 'WOMEN', 16),
('Leather Effect Sneakers', 89.90, 'Minimal lace-up sneakers with a smooth leather effect upper.', 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=80', 'UNISEX', 17),
('Canvas High Tops', 69.90, 'Canvas high top sneakers with contrast stitching and rubber sole.', 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=900&q=80', 'UNISEX', 17),
('Mini Crossbody Bag', 39.90, 'Compact crossbody bag with adjustable strap and zip closure.', 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=900&q=80', 'WOMEN', 18),
('Nylon Tote Bag', 45.90, 'Lightweight nylon tote with top handles and a detachable shoulder strap.', 'https://images.unsplash.com/photo-1551028719-00167b16eac5?auto=format&fit=crop&w=900&q=80', 'UNISEX', 18),
('Kids Printed T-Shirt', 15.90, 'Soft cotton t-shirt with a playful front print for everyday wear.', 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=80', 'KIDS', 10),
('Kids Denim Jacket', 49.90, 'Durable kids denim jacket with easy snap buttons and patch pockets.', 'https://images.unsplash.com/photo-1541099649105-f69ad21f3246?auto=format&fit=crop&w=900&q=80', 'KIDS', 12);

-- Item variants
INSERT INTO item_variants (is_active, stock_left, image_url, item_id, size_id, color_id) VALUES
(TRUE, 18, 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=80', 1, 2, 1),
(TRUE, 14, 'https://images.unsplash.com/photo-1523398002811-999ca8dec234?auto=format&fit=crop&w=900&q=80', 1, 3, 3),
(TRUE, 9, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=900&q=80', 1, 4, 2),
(TRUE, 20, 'https://images.unsplash.com/photo-1543076447-215ad9ba6923?auto=format&fit=crop&w=900&q=80', 2, 2, 4),
(TRUE, 16, 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=80', 2, 3, 1),
(TRUE, 32, 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=80', 3, 1, 1),
(TRUE, 28, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=900&q=80', 3, 2, 2),
(TRUE, 25, 'https://images.unsplash.com/photo-1523398002811-999ca8dec234?auto=format&fit=crop&w=900&q=80', 3, 3, 3),
(TRUE, 18, 'https://images.unsplash.com/photo-1543076447-215ad9ba6923?auto=format&fit=crop&w=900&q=80', 4, 1, 4),
(TRUE, 16, 'https://images.unsplash.com/photo-1496747611176-843222e1e57c?auto=format&fit=crop&w=900&q=80', 4, 2, 7),
(TRUE, 15, 'https://images.unsplash.com/photo-1542272604-787c3835535d?auto=format&fit=crop&w=900&q=80', 5, 3, 5),
(TRUE, 11, 'https://images.unsplash.com/photo-1551028719-00167b16eac5?auto=format&fit=crop&w=900&q=80', 5, 4, 8),
(TRUE, 8, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=900&q=80', 6, 2, 2),
(TRUE, 7, 'https://images.unsplash.com/photo-1543076447-215ad9ba6923?auto=format&fit=crop&w=900&q=80', 6, 3, 4),
(TRUE, 13, 'https://images.unsplash.com/photo-1541099649105-f69ad21f3246?auto=format&fit=crop&w=900&q=80', 7, 3, 6),
(TRUE, 10, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=900&q=80', 7, 4, 2),
(TRUE, 5, 'https://images.unsplash.com/photo-1542272604-787c3835535d?auto=format&fit=crop&w=900&q=80', 8, 2, 5),
(TRUE, 6, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=900&q=80', 8, 3, 2),
(TRUE, 12, 'https://images.unsplash.com/photo-1551028719-00167b16eac5?auto=format&fit=crop&w=900&q=80', 9, 4, 8),
(TRUE, 10, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=900&q=80', 9, 5, 2),
(TRUE, 19, 'https://images.unsplash.com/photo-1541099649105-f69ad21f3246?auto=format&fit=crop&w=900&q=80', 10, 2, 6),
(TRUE, 17, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=900&q=80', 10, 3, 2),
(TRUE, 12, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=900&q=80', 11, 2, 2),
(TRUE, 10, 'https://images.unsplash.com/photo-1543076447-215ad9ba6923?auto=format&fit=crop&w=900&q=80', 11, 3, 4),
(TRUE, 14, 'https://images.unsplash.com/photo-1542272604-787c3835535d?auto=format&fit=crop&w=900&q=80', 12, 3, 5),
(TRUE, 11, 'https://images.unsplash.com/photo-1523398002811-999ca8dec234?auto=format&fit=crop&w=900&q=80', 12, 4, 3),
(TRUE, 9, 'https://images.unsplash.com/photo-1496747611176-843222e1e57c?auto=format&fit=crop&w=900&q=80', 13, 2, 7),
(TRUE, 7, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=900&q=80', 13, 3, 2),
(TRUE, 13, 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=80', 14, 2, 1),
(TRUE, 10, 'https://images.unsplash.com/photo-1541099649105-f69ad21f3246?auto=format&fit=crop&w=900&q=80', 14, 3, 6),
(TRUE, 15, 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=80', 15, 11, 1),
(TRUE, 13, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=900&q=80', 15, 12, 2),
(TRUE, 11, 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=80', 16, 11, 1),
(TRUE, 9, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=900&q=80', 16, 12, 2),
(TRUE, 20, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=900&q=80', 17, 3, 2),
(TRUE, 18, 'https://images.unsplash.com/photo-1542272604-787c3835535d?auto=format&fit=crop&w=900&q=80', 17, 3, 5),
(TRUE, 22, 'https://images.unsplash.com/photo-1551028719-00167b16eac5?auto=format&fit=crop&w=900&q=80', 18, 3, 8),
(TRUE, 17, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=900&q=80', 18, 3, 2),
(TRUE, 16, 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=80', 19, 1, 1),
(TRUE, 14, 'https://images.unsplash.com/photo-1523398002811-999ca8dec234?auto=format&fit=crop&w=900&q=80', 19, 2, 3),
(TRUE, 8, 'https://images.unsplash.com/photo-1541099649105-f69ad21f3246?auto=format&fit=crop&w=900&q=80', 20, 1, 6),
(TRUE, 7, 'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=900&q=80', 20, 2, 2);

-- Addresses
INSERT INTO addresses (country, region, city, postal_code, address_line) VALUES
('Bulgaria', 'Sofia City', 'Sofia', 1000, '12 Vitosha Boulevard'),
('Bulgaria', 'Sofia City', 'Sofia', 1000, '24 Graf Ignatiev Street'),
('Bulgaria', 'Plovdiv', 'Plovdiv', 4000, '8 Knyaz Alexander I Street'),
('Bulgaria', 'Varna', 'Varna', 9000, '15 Slivnitsa Boulevard'),
('Bulgaria', 'Burgas', 'Burgas', 8000, '7 Alexandrovska Street');

INSERT INTO user_addresses (user_id, address_id) VALUES
(1, 1),
(3, 2),
(4, 3),
(5, 4),
(6, 5);

-- Favorites
INSERT INTO favorites (item_variant_id, user_id) VALUES
(6, 3),
(15, 3),
(23, 4),
(27, 4),
(31, 5),
(35, 5),
(39, 6),
(41, 6);

-- Cart items
INSERT INTO cart_items (quantity, item_variant_id, user_id) VALUES
(1, 1, 3),
(2, 7, 3),
(1, 14, 4),
(1, 24, 4),
(2, 32, 5),
(1, 36, 5),
(1, 40, 6),
(1, 42, 6);

-- Reviews
INSERT INTO reviews (body, size_fit, quality, comfort, user_id, item_id) VALUES
('The fabric feels substantial and the shirt keeps its shape after washing.', 'TRUE_TO_SIZE', 'EXCELLENT', 'VERY_COMFORTABLE', 3, 1),
('Clean cut and easy to style, but I would size up for a looser fit.', 'RUNS_SMALL', 'AVERAGE', 'COMFORTABLE', 4, 2),
('The heavyweight cotton is exactly what I wanted for everyday wear.', 'TRUE_TO_SIZE', 'EXCELLENT', 'VERY_COMFORTABLE', 5, 3),
('Soft knit with a flattering fit and no scratchy seams.', 'TRUE_TO_SIZE', 'EXCELLENT', 'VERY_COMFORTABLE', 6, 4),
('Great jacket for transitional weather, especially with denim and trainers.', 'TRUE_TO_SIZE', 'EXCELLENT', 'COMFORTABLE', 3, 7),
('The coat looks more expensive than it is and feels warm enough for winter.', 'TRUE_TO_SIZE', 'EXCELLENT', 'VERY_COMFORTABLE', 4, 8),
('Jeans are sturdy and the wash looks natural, though the first wear is firm.', 'RUNS_SMALL', 'AVERAGE', 'COMFORTABLE', 5, 10),
('The trousers drape nicely and work well with both shirts and knitwear.', 'TRUE_TO_SIZE', 'EXCELLENT', 'VERY_COMFORTABLE', 6, 11),
('Comfortable sneakers for long city walks with a very minimal look.', 'TRUE_TO_SIZE', 'EXCELLENT', 'COMFORTABLE', 3, 15),
('The tote is light, practical, and fits a laptop plus daily essentials.', 'TRUE_TO_SIZE', 'AVERAGE', 'COMFORTABLE', 4, 18),
('My child likes the print and the cotton is soft after washing.', 'TRUE_TO_SIZE', 'EXCELLENT', 'VERY_COMFORTABLE', 5, 19),
('Durable kids jacket with enough room for a hoodie underneath.', 'RUNS_LARGE', 'EXCELLENT', 'COMFORTABLE', 6, 20);

