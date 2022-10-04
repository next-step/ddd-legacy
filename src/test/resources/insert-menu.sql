SET REFERENTIAL_INTEGRITY FALSE;

insert into product (id, name, price)
values (x'3b52824434f7406bbb7e690912f66b10', '후라이드', 16000);

insert into menu_group (id, name)
values (x'cbc75faefeb04bb18be2cb8ce5d8fded', '한마리메뉴');

insert into menu (id, displayed, name, price, menu_group_id)
values (x'f59b1e1cb145440aaa6f6095a0e2d63b', true, '후라이드치킨', 16000, x'cbc75faefeb04bb18be2cb8ce5d8fded');

insert into menu (id, displayed, name, price, menu_group_id)
values (x'e1254913860846aab23aa07c1dcbc648', true, '양념치킨', 16000, x'cbc75faefeb04bb18be2cb8ce5d8fded');

insert into menu_product (quantity, product_id, menu_id)
values (1, x'3b52824434f7406bbb7e690912f66b10', x'f59b1e1cb145440aaa6f6095a0e2d63b');

insert into menu_product (quantity, product_id, menu_id)
values (1, x'3b52824434f7406bbb7e690912f66b11', x'f59b1e1cb145440aaa6f6095a0e2d63b');

SET REFERENTIAL_INTEGRITY TRUE;

