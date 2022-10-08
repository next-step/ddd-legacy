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

SET REFERENTIAL_INTEGRITY TRUE;
