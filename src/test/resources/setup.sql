insert into product (id, name, price)
values (x'3b52824434f7406bbb7e690912f66b10', '후라이드', 16000);
insert into product (id, name, price)
values (x'c5ee925c3dbb4941b825021446f24446', '양념치킨', 16000);
insert into product (id, name, price)
values (x'625c6fc4145d408f8dd533c16ba26064', '반반치킨', 16000);
insert into product (id, name, price)
values (x'4721ee722ff3417fade3acd0a804605b', '통구이', 16000);
insert into product (id, name, price)
values (x'0ac16db71b024a87b9c1e7d8f226c48d', '간장치킨', 17000);
insert into product (id, name, price)
values (x'7de4b8affa0f4391aaa9c61ea9b40f83', '순살치킨', 17000);

insert into menu_group (id, name)
values (x'f1860abc2ea1411bbd4abaa44f0d5580', '두마리메뉴');
insert into menu_group (id, name)
values (x'cbc75faefeb04bb18be2cb8ce5d8fded', '한마리메뉴');
insert into menu_group (id, name)
values (x'5e9879b761124791a4cef22e94af8752', '순살파닭두마리메뉴');
insert into menu_group (id, name)
values (x'd9bc21accc104593b5064a40e0170e02', '신메뉴');

insert into menu (id, displayed, name, price, menu_group_id)
values (x'f59b1e1cb145440aaa6f6095a0e2d63b', true, '후라이드치킨', 16000, x'cbc75faefeb04bb18be2cb8ce5d8fded');
insert into menu (id, displayed, name, price, menu_group_id)
values (x'e1254913860846aab23aa07c1dcbc648', true, '양념치킨', 16000, x'cbc75faefeb04bb18be2cb8ce5d8fded');
insert into menu (id, displayed, name, price, menu_group_id)
values (x'191fa247b5f34b51b175e65db523f754', true, '반반치킨', 16000, x'cbc75faefeb04bb18be2cb8ce5d8fded');
insert into menu (id, displayed, name, price, menu_group_id)
values (x'33e558df7d934622b50efcc4282cd184', true, '통구이', 16000, x'cbc75faefeb04bb18be2cb8ce5d8fded');
insert into menu (id, displayed, name, price, menu_group_id)
values (x'b9c670b04ef5409083496868df1c7d62', true, '간장치킨', 17000, x'cbc75faefeb04bb18be2cb8ce5d8fded');
insert into menu (id, displayed, name, price, menu_group_id)
values (x'a64af6cac34d4cd882fe454abf512d1f', true, '순살치킨', 17000, x'cbc75faefeb04bb18be2cb8ce5d8fded');

insert into menu_product (quantity, product_id, menu_id)
values (1, x'3b52824434f7406bbb7e690912f66b10', x'f59b1e1cb145440aaa6f6095a0e2d63b');
insert into menu_product (quantity, product_id, menu_id)
values (1, x'c5ee925c3dbb4941b825021446f24446', x'e1254913860846aab23aa07c1dcbc648');
insert into menu_product (quantity, product_id, menu_id)
values (1, x'625c6fc4145d408f8dd533c16ba26064', x'191fa247b5f34b51b175e65db523f754');
insert into menu_product (quantity, product_id, menu_id)
values (1, x'4721ee722ff3417fade3acd0a804605b', x'33e558df7d934622b50efcc4282cd184');
insert into menu_product (quantity, product_id, menu_id)
values (1, x'0ac16db71b024a87b9c1e7d8f226c48d', x'b9c670b04ef5409083496868df1c7d62');
insert into menu_product (quantity, product_id, menu_id)
values (1, x'7de4b8affa0f4391aaa9c61ea9b40f83', x'a64af6cac34d4cd882fe454abf512d1f');

insert into order_table (id, occupied, name, number_of_guests)
values (x'8d71004329b6420e8452233f5a035520', false, '1번', 0);
insert into order_table (id, occupied, name, number_of_guests)
values (x'6ab59e8106eb441684e99faabc87c9ca', false, '2번', 0);
insert into order_table (id, occupied, name, number_of_guests)
values (x'ae92335ccd264626b7979e4ae8c4efbd', false, '3번', 0);
insert into order_table (id, occupied, name, number_of_guests)
values (x'a9858d4b80d0428881f48f41596a23fb', false, '4번', 0);
insert into order_table (id, occupied, name, number_of_guests)
values (x'3faec3ab5217405daaa2804f87697f84', false, '5번', 0);
insert into order_table (id, occupied, name, number_of_guests)
values (x'815b8395a2ad4e3589dc74c3b2191478', false, '6번', 0);
insert into order_table (id, occupied, name, number_of_guests)
values (x'7ce8b3a235454542ab9cb3d493bbd4fb', false, '7번', 0);
insert into order_table (id, occupied, name, number_of_guests)
values (x'7bdb1ffde36e4e2b94e3d2c14d391ef3', false, '8번', 0);

insert into orders (id, delivery_address, order_date_time, status, type, order_table_id)
values (x'69d78f383bff457cbb7226319c985fd8', '서울시 송파구 위례성대로 2', '2021-07-27', 'WAITING', 'DELIVERY', null);
insert into orders (id, delivery_address, order_date_time, status, type, order_table_id)
values (x'98da3d3859e04dacbbaeebf6560a43bd', null, '2021-07-27', 'COMPLETED', 'EAT_IN',
        x'8d71004329b6420e8452233f5a035520');
insert into orders (id, delivery_address, order_date_time, status, type, order_table_id)
values (x'd7cc15b3e32c4bc8b440d3067b35522e', null, '2021-07-27', 'COMPLETED', 'EAT_IN',
        x'8d71004329b6420e8452233f5a035520');

insert into order_line_item (quantity, menu_id, order_id)
values (1, x'f59b1e1cb145440aaa6f6095a0e2d63b', x'69d78f383bff457cbb7226319c985fd8');
insert into order_line_item (quantity, menu_id, order_id)
values (1, x'f59b1e1cb145440aaa6f6095a0e2d63b', x'98da3d3859e04dacbbaeebf6560a43bd');
insert into order_line_item (quantity, menu_id, order_id)
values (1, x'f59b1e1cb145440aaa6f6095a0e2d63b', x'd7cc15b3e32c4bc8b440d3067b35522e');
