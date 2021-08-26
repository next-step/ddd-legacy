insert into menu (id, displayed, name, price, menu_group_id)
values (x'f76c720e8c1346739a24cf385654159d', false, '비싼후라이드치킨', 16001, x'cbc75faefeb04bb18be2cb8ce5d8fded');

insert into menu_product (quantity, product_id, menu_id)
values (1, x'3b52824434f7406bbb7e690912f66b10', x'f76c720e8c1346739a24cf385654159d');
