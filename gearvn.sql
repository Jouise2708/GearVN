CREATE DATABASE gearvn;

USE gearvn;

CREATE TABLE users (
    id BIGINT IDENTITY PRIMARY KEY,
    email NVARCHAR(255),
    password NVARCHAR(255),
    name NVARCHAR(255)
);

INSERT INTO users (email, password, name) VALUES
(N'admin@gmail.com', N'123456', N'Admin'),
(N'user@gmail.com', N'123456', N'User');


CREATE TABLE products (
    id BIGINT IDENTITY PRIMARY KEY,
    name NVARCHAR(255),
    price FLOAT,
    description NVARCHAR(MAX)
);

ALTER TABLE products
ADD CONSTRAINT UQ_product_name UNIQUE (name);

INSERT INTO products (name, price, description) VALUES

-- ================= LAPTOP =================
(N'Laptop ASUS ROG Strix G15', 31790000, N'Laptop gaming hiệu năng cao, RTX 4060, Ryzen 7, màn 144Hz'),
(N'Laptop ASUS ROG Zephyrus G14', 48900000, N'Thiết kế mỏng nhẹ, cấu hình mạnh, phù hợp gaming và đồ họa'),
(N'Laptop MSI Katana 15', 28900000, N'Laptop gaming giá tốt, Intel i7, RTX 4050'),
(N'Laptop Acer Nitro 5', 25900000, N'Dòng gaming phổ thông, hiệu năng ổn định'),
(N'Laptop Lenovo Legion 5', 32900000, N'Máy gaming cao cấp, tản nhiệt tốt'),
(N'Laptop ASUS TUF Gaming F15', 23900000, N'Laptop gaming bền bỉ, giá tốt'),

-- ================= CHUỘT =================
(N'Chuột Razer DeathAdder Essential', 590000, N'Chuột gaming phổ biến, cảm biến tốt'),
(N'Chuột Razer Basilisk Ultimate', 3490000, N'Chuột không dây cao cấp, RGB đẹp'),
(N'Chuột Logitech G102', 390000, N'Chuột gaming giá rẻ, phù hợp mọi người'),
(N'Chuột Logitech G Pro X Superlight', 2990000, N'Chuột siêu nhẹ dành cho eSports'),
(N'Chuột Corsair M65 RGB Elite', 1290000, N'Chuột gaming cao cấp, thiết kế đẹp'),
(N'Chuột SteelSeries Rival 3', 890000, N'Chuột gaming tầm trung'),

-- ================= KEYBOARD =================
(N'Bàn phím cơ AULA F75', 650000, N'Bàn phím cơ giá rẻ, switch tốt'),
(N'Bàn phím AKKO 3098', 1290000, N'Bàn phím cơ custom đẹp'),
(N'Bàn phím Corsair K70 RGB', 3490000, N'Bàn phím cao cấp RGB'),
(N'Bàn phím Logitech G Pro X', 2990000, N'Bàn phím gaming chuyên nghiệp'),
(N'Bàn phím Razer BlackWidow V3', 2890000, N'Bàn phím gaming nổi tiếng'),
(N'Bàn phím DareU EK87', 790000, N'Bàn phím cơ phổ thông'),

-- ================= HEADPHONE =================
(N'Tai nghe Razer Barracuda X', 2890000, N'Tai nghe gaming không dây'),
(N'Tai nghe HyperX Cloud II', 2190000, N'Âm thanh tốt, mic rõ'),
(N'Tai nghe Logitech G733', 2990000, N'Thiết kế đẹp, không dây'),
(N'Tai nghe Corsair HS80 RGB', 3290000, N'Âm thanh vòm chất lượng cao'),
(N'Tai nghe SteelSeries Arctis 5', 2590000, N'Âm thanh gaming chuẩn'),
(N'Tai nghe ASUS TUF H3', 1290000, N'Giá tốt, dùng ổn'),

-- ================= MONITOR =================
(N'Màn hình ASUS TUF 24 inch 144Hz', 4590000, N'Màn gaming 144Hz mượt'),
(N'Màn hình MSI 27 inch 165Hz', 5890000, N'Màn lớn, tần số cao'),
(N'Màn hình LG UltraGear 27GL850', 8990000, N'Màn IPS cao cấp'),
(N'Màn hình Samsung Odyssey G5', 7990000, N'Màn cong gaming'),

-- ================= PC PART =================
(N'CPU Intel Core i5 13400F', 5290000, N'CPU gaming tầm trung mạnh'),
(N'CPU AMD Ryzen 5 5600X', 4990000, N'CPU hiệu năng cao'),
(N'GPU RTX 4060', 9990000, N'Card đồ họa mới'),
(N'GPU RTX 4070', 15990000, N'Card đồ họa cao cấp'),
(N'RAM Corsair 16GB DDR4', 990000, N'RAM gaming'),
(N'SSD Samsung 980 1TB', 1890000, N'Ổ cứng SSD tốc độ cao');

CREATE TABLE cart_items (
    id BIGINT IDENTITY PRIMARY KEY,
    user_id BIGINT,
    product_id BIGINT,
    quantity INT,

    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

INSERT INTO cart_items (user_id, product_id, quantity) VALUES
(1, 1, 1),
(1, 2, 2),
(2, 3, 1);


CREATE TABLE orders (
    id BIGINT IDENTITY PRIMARY KEY,
    user_id BIGINT,
    total FLOAT,
    status NVARCHAR(50),
    created_at DATETIME DEFAULT GETDATE(),

    FOREIGN KEY (user_id) REFERENCES users(id)
);

INSERT INTO orders (user_id, total, status) VALUES
(1, 24000000, N'PENDING'),
(2, 500000, N'COMPLETED');


CREATE TABLE order_items (
    id BIGINT IDENTITY PRIMARY KEY,
    order_id BIGINT,
    product_id BIGINT,
    quantity INT,
    price FLOAT,

    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

INSERT INTO order_items (order_id, product_id, quantity, price) VALUES
(1, 1, 1, 9000000),
(1, 2, 1, 15000000),
(2, 4, 1, 500000);


SELECT * FROM products

SELECT * FROM users WHERE email = 'admin@gmail.com'

select * from customers

SELECT * FROM users

SELECT * FROM orders

DELETE FROM products;




