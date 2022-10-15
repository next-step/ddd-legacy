SET REFERENTIAL_INTEGRITY FALSE;

insert into order_table (id, occupied, name, number_of_guests)
values (x'8d71004329b6420e8452233f5a035520', false, '1번', 0);

insert into order_table (id, occupied, name, number_of_guests)
values (x'8d71004329b6420e8452233f5a035521', true, '1번', 0);

insert into order_table (id, occupied, name, number_of_guests)
values (x'8d71004329b6420e8452233f5a035522', true, '1번', 1);

insert into orders (id, delivery_address, order_date_time, status, type, order_table_id)
values (x'69d78f383bff457cbb7226319c985fd8', '서울시 송파구 위례성대로 2', '2021-07-27', 'COMPLETED', 'DELIVERY', x'8d71004329b6420e8452233f5a035521');

insert into orders (id, delivery_address, order_date_time, status, type, order_table_id)
values (x'69d78f383bff457cbb7226319c985fd9', '서울시 송파구 위례성대로 2', '2021-07-27', 'WAITING', 'DELIVERY', x'8d71004329b6420e8452233f5a035522');

SET REFERENTIAL_INTEGRITY TRUE;
