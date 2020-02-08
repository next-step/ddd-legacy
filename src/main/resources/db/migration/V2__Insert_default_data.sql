INSERT INTO menu_group (id, name) VALUES (1, '두마리메뉴');
INSERT INTO menu_group (id, name) VALUES (2, '한마리메뉴');
INSERT INTO menu_group (id, name) VALUES (3, '순살파닭두마리메뉴');
INSERT INTO menu_group (id, name) VALUES (4, '신메뉴');
INSERT INTO menu_group (id, name) VALUES (5, '저세상메뉴');

INSERT INTO product (id, name, price) VALUES (1, '후라이드', 16000);
INSERT INTO product (id, name, price) VALUES (2, '양념치킨', 16000);
INSERT INTO product (id, name, price) VALUES (3, '반반치킨', 16000);
INSERT INTO product (id, name, price) VALUES (4, '통구이', 16000);
INSERT INTO product (id, name, price) VALUES (5, '간장치킨', 17000);
INSERT INTO product (id, name, price) VALUES (6, '순살치킨', 17000);
INSERT INTO product (id, name, price) VALUES (7, '저세상치킨', 5000000);
INSERT INTO product (id, name, price) VALUES (8, '저세상감튀', 400000);
INSERT INTO product (id, name, price) VALUES (9, '저세상콜라', 10000);

INSERT INTO menu (id, name, price, menu_group_id) VALUES (1, '후라이드치킨', 16000, 2);
INSERT INTO menu (id, name, price, menu_group_id) VALUES (2, '양념치킨', 16000, 2);
INSERT INTO menu (id, name, price, menu_group_id) VALUES (3, '반반치킨', 16000, 2);
INSERT INTO menu (id, name, price, menu_group_id) VALUES (4, '통구이', 16000, 2);
INSERT INTO menu (id, name, price, menu_group_id) VALUES (5, '간장치킨', 17000, 2);
INSERT INTO menu (id, name, price, menu_group_id) VALUES (6, '순살치킨', 17000, 2);
INSERT INTO menu (id, name, price, menu_group_id) VALUES (7, '저세상세트', 5350000, 5);

INSERT INTO menu_product (menu_id, product_id, quantity) VALUES (1, 1, 1);
INSERT INTO menu_product (menu_id, product_id, quantity) VALUES (2, 2, 1);
INSERT INTO menu_product (menu_id, product_id, quantity) VALUES (3, 3, 1);
INSERT INTO menu_product (menu_id, product_id, quantity) VALUES (4, 4, 1);
INSERT INTO menu_product (menu_id, product_id, quantity) VALUES (5, 5, 1);
INSERT INTO menu_product (menu_id, product_id, quantity) VALUES (6, 6, 1);
INSERT INTO menu_product (menu_id, product_id, quantity) VALUES (7, 7, 1);
INSERT INTO menu_product (menu_id, product_id, quantity) VALUES (7, 8, 1);
INSERT INTO menu_product (menu_id, product_id, quantity) VALUES (7, 9, 2);


INSERT INTO order_table (id, number_of_guests, empty) VALUES (1, 0, true);
INSERT INTO order_table (id, number_of_guests, empty) VALUES (2, 0, true);
INSERT INTO order_table (id, number_of_guests, empty) VALUES (3, 0, true);
INSERT INTO order_table (id, number_of_guests, empty) VALUES (4, 0, true);
INSERT INTO order_table (id, number_of_guests, empty) VALUES (5, 0, true);
INSERT INTO order_table (id, number_of_guests, empty) VALUES (6, 0, true);
INSERT INTO order_table (id, number_of_guests, empty) VALUES (7, 0, true);
INSERT INTO order_table (id, number_of_guests, empty) VALUES (8, 0, true);
INSERT INTO order_table (id, number_of_guests, empty) VALUES (9, 0, false);
