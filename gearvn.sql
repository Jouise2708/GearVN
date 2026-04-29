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
(N'RTX 4060', 9000000, N'VGA manh'),
(N'RTX 4070', 15000000, N'VGA cao cap'),
(N'Laptop ASUS ROG', 25000000, N'Gaming laptop'),
(N'Chuot Logitech', 500000, N'Chuot gaming');


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













select * from customers

SELECT * FROM users

SELECT * FROM orders

DELETE FROM products;

DROP TABLE customers

