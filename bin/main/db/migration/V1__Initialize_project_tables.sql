CREATE TABLE orders (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    order_table_id BIGINT(20) NOT NULL,
    order_status VARCHAR(255) NOT NULL,
    ordered_time DATETIME NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE order_line_item (
    seq BIGINT(20) NOT NULL AUTO_INCREMENT,
    order_id BIGINT(20) NOT NULL,
    menu_id BIGINT(20) NOT NULL,
    quantity BIGINT(20) NOT NULL,
    PRIMARY KEY (seq)
);

CREATE TABLE menu (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    menu_group_id BIGINT(20) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE menu_group (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE menu_product (
    seq BIGINT(20) NOT NULL AUTO_INCREMENT,
    menu_id BIGINT(20) NOT NULL,
    product_id BIGINT(20) NOT NULL,
    quantity BIGINT(20) NOT NULL,
    PRIMARY KEY (seq)
);

CREATE TABLE order_table (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    table_group_id BIGINT(20),
    number_of_guests INT(11) NOT NULL,
    empty BIT(1) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE table_group (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    created_date DATETIME NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE product (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_order_table
        FOREIGN KEY (order_table_id) REFERENCES order_table (id);

ALTER TABLE order_line_item
    ADD CONSTRAINT fk_order_line_item_orders
        FOREIGN KEY (order_id) REFERENCES orders (id);

ALTER TABLE order_line_item
    ADD CONSTRAINT fk_order_line_item_menu
        FOREIGN KEY (menu_id) REFERENCES menu (id);

ALTER TABLE menu
    ADD CONSTRAINT fk_menu_menu_group
        FOREIGN KEY (menu_group_id) REFERENCES menu_group (id);

ALTER TABLE menu_product
    ADD CONSTRAINT fk_menu_product_menu
        FOREIGN KEY (menu_id) REFERENCES menu (id);

ALTER TABLE menu_product
    ADD CONSTRAINT fk_menu_product_product
        FOREIGN KEY (product_id) REFERENCES product (id);

ALTER TABLE order_table
    ADD CONSTRAINT fk_order_table_table_group
        FOREIGN KEY (table_group_id) REFERENCES table_group (id);
