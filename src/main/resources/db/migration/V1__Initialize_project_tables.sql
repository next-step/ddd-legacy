create table menu
(
    id            binary(16)     not null,
    displayed     bit            not null,
    name          varchar(255)   not null,
    price         decimal(19, 2) not null,
    menu_group_id binary(16)     not null,
    primary key (id)
) engine = InnoDB;

create table menu_group
(
    id   binary(16)   not null,
    name varchar(255) not null,
    primary key (id)
) engine = InnoDB;

create table menu_product
(
    seq        bigint     not null auto_increment,
    quantity   bigint     not null,
    product_id binary(16) not null,
    menu_id    binary(16) not null,
    primary key (seq)
) engine = InnoDB;

create table order_line_item
(
    seq      bigint     not null auto_increment,
    quantity bigint     not null,
    menu_id  binary(16) not null,
    order_id binary(16) not null,
    primary key (seq)
) engine = InnoDB;

create table order_table
(
    id               binary(16)   not null,
    occupied         bit          not null,
    name             varchar(255) not null,
    number_of_guests integer      not null,
    primary key (id)
) engine = InnoDB;

create table orders
(
    id               binary(16)   not null,
    delivery_address varchar(255),
    order_date_time  datetime(6)  not null,
    status           varchar(255) not null,
    type             varchar(255) not null,
    order_table_id   binary(16),
    primary key (id)
) engine = InnoDB;

create table product
(
    id    binary(16)     not null,
    name  varchar(255)   not null,
    price decimal(19, 2) not null,
    primary key (id)
) engine = InnoDB;

alter table menu
    add constraint fk_menu_to_menu_group
        foreign key (menu_group_id)
            references menu_group (id);

alter table menu_product
    add constraint fk_menu_product_to_product
        foreign key (product_id)
            references product (id);

alter table menu_product
    add constraint fk_menu_product_to_menu
        foreign key (menu_id)
            references menu (id);

alter table order_line_item
    add constraint fk_order_line_item_to_menu
        foreign key (menu_id)
            references menu (id);

alter table order_line_item
    add constraint fk_order_line_item_to_orders
        foreign key (order_id)
            references orders (id);

alter table orders
    add constraint fk_orders_to_order_table
        foreign key (order_table_id)
            references order_table (id);
