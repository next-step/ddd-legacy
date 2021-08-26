insert into menu (id, displayed, name, price, menu_group_id)
values (x'f76c720e8c1346739a24cf385654159d', false, '비싼후라이드치킨', 16001, x'cbc75faefeb04bb18be2cb8ce5d8fded');

insert into menu_product (quantity, product_id, menu_id)
values (1, x'3b52824434f7406bbb7e690912f66b10', x'f76c720e8c1346739a24cf385654159d');

insert into order_table (id, empty, name, number_of_guests)
values (x'7cb011e0c1d14cf1b217d2272d8f2545', false, '9번', 2);

insert into orders (id, delivery_address, order_date_time, status, type, order_table_id)
values (x'c1f3591cabe54ac990b08f3b97435ca7', null, '2021-07-27', 'SERVED', 'EAT_IN',
        x'7cb011e0c1d14cf1b217d2272d8f2545');

insert into order_line_item (quantity, menu_id, order_id)
values (1, x'f59b1e1cb145440aaa6f6095a0e2d63b', x'c1f3591cabe54ac990b08f3b97435ca7');

insert into orders (id, delivery_address, order_date_time, status, type, order_table_id)
values (x'2a802c284278423a82c427a4ea3591b4', null, '2021-07-27', 'WAITING', 'EAT_IN',
    x'7cb011e0c1d14cf1b217d2272d8f2545');

insert into order_line_item (quantity, menu_id, order_id)
values (1, x'e1254913860846aab23aa07c1dcbc648', x'2a802c284278423a82c427a4ea3591b4');
