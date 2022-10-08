SET REFERENTIAL_INTEGRITY FALSE;

insert into menu_group (id, name)
values (x'cbc75faefeb04bb18be2cb8ce5d8fded', '한마리메뉴');

insert into menu (id, displayed, name, price, menu_group_id)
values (x'f59b1e1cb145440aaa6f6095a0e2d63b', true, '후라이드치킨', 16000, x'cbc75faefeb04bb18be2cb8ce5d8fded');

insert into menu (id, displayed, name, price, menu_group_id)
values (x'f59b1e1cb145440aaa6f6095a0e2d63c', false, '양념치킨', 16000, x'cbc75faefeb04bb18be2cb8ce5d8fded');

insert into order_table (id, occupied, name, number_of_guests)
values (x'8d71004329b6420e8452233f5a035520', true, '1번', 0);

insert into order_table (id, occupied, name, number_of_guests)
values (x'8d71004329b6420e8452233f5a035521', false, '1번', 0);

insert into order_table (id, occupied, name, number_of_guests)
values (x'8d71004329b6420e8452233f5a035522', true, '1번', 1);


insert into orders (id, delivery_address, order_date_time, status, type, order_table_id)
values (x'69d78f383bff457cbb7226319c985fd8', '서울시 송파구 위례성대로 2', '2021-07-27', 'WAITING', 'DELIVERY', null);

insert into orders (id, delivery_address, order_date_time, status, type, order_table_id)
values (x'79d78f383bff457cbb7226319c985fd8', '서울시 송파구 위례성대로 2', '2021-07-27', 'ACCEPTED', 'TAKEOUT', null);

insert into orders (id, delivery_address, order_date_time, status, type, order_table_id)
values (x'89d78f383bff457cbb7226319c985fd8', '서울시 송파구 위례성대로 2', '2021-07-27', 'SERVED', 'DELIVERY', null);

insert into orders (id, delivery_address, order_date_time, status, type, order_table_id)
values (x'99d78f383bff457cbb7226319c985fd8', '서울시 송파구 위례성대로 2', '2021-07-27', 'DELIVERING', 'DELIVERY', null);

insert into orders (id, delivery_address, order_date_time, status, type, order_table_id)
values (x'09d78f383bff457cbb7226319c985fd8', '서울시 송파구 위례성대로 2', '2021-07-27', 'DELIVERED', 'DELIVERY', null);

insert into orders (id, delivery_address, order_date_time, status, type, order_table_id)
values (x'19d78f383bff457cbb7226319c985fd8', '서울시 송파구 위례성대로 2', '2021-07-27', 'ACCEPTED', 'TAKEOUT', null);

insert into orders (id, delivery_address, order_date_time, status, type, order_table_id)
values (x'29d78f383bff457cbb7226319c985fd8', '서울시 송파구 위례성대로 2', '2021-07-27', 'ACCEPTED', 'EAT_IN', null);

insert into orders (id, delivery_address, order_date_time, status, type, order_table_id)
values (x'39d78f383bff457cbb7226319c985fd8', '서울시 송파구 위례성대로 2', '2021-07-27', 'SERVED', 'EAT_IN', x'8d71004329b6420e8452233f5a035522');

SET REFERENTIAL_INTEGRITY TRUE;
