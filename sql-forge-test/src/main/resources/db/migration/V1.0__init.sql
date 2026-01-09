-- 启用 H2 的 UUID 支持（如果需要显式使用 UUID 函数，但本例中 UUID 由应用提供，所以仅作注释说明）
-- 注意：H2 2.x+ 默认支持 UUID 类型，但本脚本使用 VARCHAR(36) 存储 UUID 字符串以兼容性更好

-- 1. 创建 users 表（UUID 主键，应用生成）
CREATE TABLE users (
                       id VARCHAR(36) NOT NULL PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       email VARCHAR(100)
);

-- 2. 创建 products 表（UUID 主键，应用生成）
CREATE TABLE products (
                          id VARCHAR(36) NOT NULL PRIMARY KEY,
                          name VARCHAR(100) NOT NULL,
                          price DECIMAL(10, 2)
);

-- 3. 创建 orders 表（自增主键）
CREATE TABLE orders (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        user_id VARCHAR(36) NOT NULL,
                        product_id VARCHAR(36) NOT NULL,
                        order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        quantity INT NOT NULL DEFAULT 1,
                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- 插入测试用户数据（使用预定义 UUID）
INSERT INTO users (id, username, email) VALUES
                                            ('550e8400-e29b-41d4-a716-446655440000', 'alice', 'alice@example.com'),
                                            ('550e8400-e29b-41d4-a716-446655440001', 'bob', 'bob@example.com'),
                                            ('550e8400-e29b-41d4-a716-446655440002', 'charlie', 'charlie@example.com'),
                                            ('550e8400-e29b-41d4-a716-446655440003', 'wubo01', 'wubo01@@example.com'),
                                            ('550e8400-e29b-41d4-a716-446655440004', 'wubo02', 'wubo02@example.com'),
                                            ('550e8400-e29b-41d4-a716-446655440005', 'wubo03', 'wubo03@example.com'),
                                            ('550e8400-e29b-41d4-a716-446655440006', 'wubo04', 'wubo04@example.com'),
                                            ('550e8400-e29b-41d4-a716-446655440007', 'wubo05', 'wubo05@example.com'),
                                            ('550e8400-e29b-41d4-a716-446655440008', 'wubo06', 'wubo06@example.com'),
                                            ('550e8400-e29b-41d4-a716-446655440009', 'wubo07', 'wubo07@example.com'),
                                            ('550e8400-e29b-41d4-a716-446655440010', 'wubo08', 'wubo08@example.com'),
                                            ('550e8400-e29b-41d4-a716-446655440011', 'wubo09', 'wubo09@example.com'),
                                            ('550e8400-e29b-41d4-a716-446655440012', 'wubo10', 'wubo10@example.com'),
                                            ('550e8400-e29b-41d4-a716-446655440013', 'wubo11', 'wubo11@example.com'),
                                            ('550e8400-e29b-41d4-a716-446655440014', 'wubo12', 'wubo12@example.com'),
                                            ('550e8400-e29b-41d4-a716-446655440015', 'wubo13', 'wubo13@example.com'),
                                            ('550e8400-e29b-41d4-a716-446655440016', 'wubo14', 'wubo14@example.com'),
                                            ('550e8400-e29b-41d4-a716-446655440017', 'wubo15', 'wubo15@example.com'),
                                            ('550e8400-e29b-41d4-a716-446655440018', 'wubo16', 'wubo16@example.com'),
                                            ('550e8400-e29b-41d4-a716-446655440019', 'wubo17', 'wubo17@example.com'),
                                            ('550e8400-e29b-41d4-a716-446655440020', 'wubo18', 'wubo18@example.com'),
                                            ('550e8400-e29b-41d4-a716-446655440021', 'wubo19', 'wubo19@example.com'),
                                            ('550e8400-e29b-41d4-a716-446655440022', 'wubo20', 'wubo20@example.com');

-- 插入测试商品数据
INSERT INTO products (id, name, price) VALUES
                                           ('f47ac10b-58cc-4372-a567-0e02b2c3d479', 'Laptop', 999.99),
                                           ('f47ac10b-58cc-4372-a567-0e02b2c3d480', 'Mouse', 25.50),
                                           ('f47ac10b-58cc-4372-a567-0e02b2c3d481', 'Keyboard', 75.00);

-- 插入测试订单数据（自增 id）
INSERT INTO orders (user_id, product_id, quantity) VALUES
                                                       ('550e8400-e29b-41d4-a716-446655440000', 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 1),
                                                       ('550e8400-e29b-41d4-a716-446655440000', 'f47ac10b-58cc-4372-a567-0e02b2c3d480', 2),
                                                       ('550e8400-e29b-41d4-a716-446655440001', 'f47ac10b-58cc-4372-a567-0e02b2c3d481', 1),
                                                       ('550e8400-e29b-41d4-a716-446655440002', 'f47ac10b-58cc-4372-a567-0e02b2c3d479', 1);

-- 示例联表查询：查询每个订单的用户、商品信息
SELECT
    o.id AS order_id,
    u.username,
    p.name AS product_name,
    p.price,
    o.quantity,
    (p.price * o.quantity) AS total
FROM orders o
JOIN users u ON o.user_id = u.id
JOIN products p ON o.product_id = p.id;