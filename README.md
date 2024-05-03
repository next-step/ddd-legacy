# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항

- 공통
    - UUID와 자동 증감값을 사용한다.
- 메뉴 그룹 (menu_group)
    - [ ] 메뉴 그룹은 UUID로 식별한다.
    - [ ] 새로운 메뉴 그룹을 등록할 수 있다.
        - 메뉴 그룹 안에는 메뉴의 이름들이 포함된다.
    - [ ] 메뉴 그룹들을 조회할 수 있다.
- 메뉴 (menu)
    - [ ] 메뉴를 등록할 수 있다.
        - 이름 , 가격 , 메뉴 그룹 아이디 , 공개여부 , 메뉴 상품들로 구성 있다.
        - 메뉴 상품들은 상품 아이디와 수량으로 구성된다.
    - [ ] 메뉴의 아이디를 통해 가격을 수정 할 수 있다.
    - [ ] 메뉴의 아이디를 통해 공개 처리 할 수 있다.
    - [ ] 메뉴의 아이디를 통해 비공개 처리 할 수 있다.
    - [ ] 메뉴들을 조회할 수 있다.
- 주문 테이블 (order_table)
- 주문 (order)
- 상품 (product)

## 용어 사전

| 한글명 | 영문명 | 설명 |
|-----|-----|----|
|     |     |    |

## 모델링

```mermaid
classDiagram

%% 모델간 관계
    Order <|-- OrderLineItem
    MenuGroup <|-- Menu
    Menu <|-- MenuProduct
    Menu <|-- OrderLineItem
    Product <|-- MenuProduct
    OrderTable <|-- Order
%% 메뉴의 구성
    Menu: +binary(16) id
    Menu: +bit displayed
    Menu: +varchar(255) name
    Menu: +decimal(19, 2) price
    Menu: +binary(16) menu_group_id
%% 메뉴 그룹의 구성
    MenuGroup: +binary(16) id
    MenuGroup: +varchar(255) name
%% 메뉴와 상품의 관계
    MenuProduct: +bigint seq
    MenuProduct: +bigint quantity
    MenuProduct: +binary(16) product_id
    MenuProduct: +binary(16) menu_id
%% 주문의 구성
    OrderLineItem: +bigint seq
    OrderLineItem: +bigint quantity
    OrderLineItem: +binary(16) menu_id
    OrderLineItem: +binary(16) order_id
%% 주문 테이블의 구성
    OrderTable: +binary(16) id
    OrderTable: +bit occupied
    OrderTable: +varchar(255) name
    OrderTable: +integer number_of_guests
%% 주문의 구성
    Order: +binary(16) id
    Order: +varchar(255) delivery_address
    Order: +datetime(6) order_date_time
    Order: +varchar(255) status
    Order: +varchar(255) type
    Order: +binary(16) order_table_id
%% 상품의 구성
    Product: +binary(16) id
    Product: +varchar(255) name
    Product: +decimal(19, 2) price
```
