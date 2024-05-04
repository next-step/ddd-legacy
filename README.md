# 키친포스

## How to run

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항

- 메뉴 애그리거트과 주문 애그리거트으로 구분 될 수 있습니다.

## Menu Aggregate

### MenuGroup

- 메뉴는 하나 이상의 메뉴로 구성될 수 있습니다.
- 메뉴 그룹은 각각 고유한 걔념을 가질 수 있습니다.
    - ex. `추천메뉴` ,`순살파닭두마리메뉴` ,`한마리메뉴` ,`신메뉴` ,`두마리메뉴`
- `POST /api/menu-groups` 요청을 통해 메뉴 그룹을 등록 할 수 있습니다.
    - `id` , `name` 필드로 구성되어있습니다.
    - `id`는 uuid로 부여/식별됩니다.
    - `name` 필드는 필수입니다.
    - `name` 필드는 고유한 값이어야 합니다.
- `GET /api/menu-groups` 요청을 통해 메뉴 그룹 목록을 조회 할 수 있습니다.

### Product

- 메뉴는 하나 이상의 상품으로 구성될 수 있습니다.
    - 예를 들어 `후라이드 치킨 세트` **메뉴**에는 `후라이드 치킨`과 `콜라`가 포함될 수 있습니다.
- 상품의 아이디는 uuid로 식별됩니다.
- `POST /api/products` 요청을 통해 상품을 등록 할 수 있습니다.
    - `id` , `name` , `price` 필드로 구성되어있습니다.
    - `id`는 uuid로 부여/식별됩니다.
    - `name` 과 `price` 필드는 필수입니다.
    - `price` 필드는 0보다 큰 값이어야 합니다.
- `PUT /api/products/${productId}/price` 요청을 통해 상품의 가격을 수정 할 수 있습니다.
    - 경로의 `${productId}` 는 상품의 아이디 입니다.
    - `price` 필드는 필수입니다.
    - `price` 필드는 0보다 큰 값이어야 합니다.
- `GET /api/products` 요청을 통해 상품 목록을 조회 할 수 있습니다.

### Menu Product

- 메뉴와 상품은 다대다 관계입니다.
    - 예를 들어 `후라이드 치킨 세트` **메뉴**에는 `후라이드 치킨`과 `콜라`가 포함될 수 있습니다.
    - 예를 들어 `후라이드 치킨` **상품**은 `후라이드 치킨 세트`와 `후라이드 치킨 반마리` **메뉴**에 포함될 수 있습니다.
    - 복잡성을 줄이기 위해 `menu` 와 `product` 의 관계를 해소하는 `menu_product` 테이블을 사용합니다.
- 메뉴 상품 엔티티는 메뉴를 등록 시 함께 등록됩니다.
    - 개별적으로 등록할 수 없습니다.
    - `seq`, `quantity` , `product_id` , `menu_id` 필드로 구성되어있습니다.
    - `seq`는 자동 증가되는 값입니다.
    - `seq`를 제외한 나머지 필드는 필수입니다.
    - `quantity` 1 이상의 값이어야 합니다.
    - `product_id`는 상품의 아이디입니다. product 테이블의 아이디와 연결됩니다.
    - `menu_id`는 메뉴의 아이디입니다. menu 테이블의 아이디와 연결됩니다.

### Menu

- 주문은 하나 이상의 메뉴로 구성될 수 있습니다.
- 하나의 메뉴 안에 여러 상품이 포함될 수 있습니다. (다대다 관계)
    - 이런 복잡성을 해소하기 위해 `menu_product` 테이블을 사용합니다.
    - `menu` 와 `menu_product`와 1:N 관계를 가집니다.
- `POST /api/menus` 요청을 통해 메뉴를 등록할 수 있습니다.
    - `id`, `displayed`, `name`, `price`, `menu_group_id` 필드로 구성되어있습니다.
    - `id`는 uuid로 부여/식별됩니다.
    - `displayed`는 bit 값으로, 메뉴가 화면에 표시되는지 여부를 나타냅니다.
    - `name`, `price`, `menu_group_id` 필드는 필수입니다.
    - `name` 필드는 고유한 값이어야 합니다.
    - `price` 필드는 0보다 큰 값이어야 합니다.
    - `price` 는 **decimal(19,2)** 으로 최대 19 자릿수를 가질 수 있고 소수점 2자리까지 표현할 수 있습니다.
    - 등록 시 하나 이상의 `menu_product`를 함께 등록해야합니다.
    - `menu_product`는 `product_id`와 `quantity` 필드로 구성되어있습니다.
        - `product_id`는 메뉴에 포함될 상품의 아이디입니다.
        - `quantity`는 상품의 수량입니다.
- `PUT /api/menus/${menuId}/price` 요청을 통해 메뉴의 가격을 수정할 수 있습니다.
    - 경로의 `${menuId}`는 메뉴의 아이디입니다.
    - `price` 필드는 필수입니다.
    - `price` 필드는 0보다 큰 값이어야 합니다.
- `PUT /api/menus/${menuId}/display` 요청을 통해 메뉴를 화면에 표시할 수 있습니다.
    - 경로의 `${menuId}`는 메뉴의 아이디입니다.
    - 이미 화면에 표시되는 메뉴는 무시됩니다.
- `PUT /api/menus/${menuId}/hide` 요청을 통해 메뉴를 화면에서 숨길 수 있습니다.
    - 경로의 `${menuId}`는 메뉴의 아이디입니다.
    - 이미 화면에서 숨겨진 메뉴는 무시됩니다.
- `GET /api/menus` 요청을 통해 메뉴 목록을 조회할 수 있습니다.

## Order Aggregate

### Order

- 고객은 주문을 생성할 수 있습니다.
- 주문은 하나 이상의 주문항목으로 구성됩니다.
- 고객은  **DELIVERY(배달)**, **TAKEOUT(포장)**, **EAT_IN(매장식사)** 중 하나를 선택해 주문 할 수 있습니다.
- 고객은 **DELIVERY(배달)** 타입의 주문시 배달 주소를 제공해야한다.
- 고객은 **EAT_IN(매장식사)** 타입의 주문시 테이블을 선택해야한다.
- `POST /api/orders` 요청을 통해 주문을 생성할 수 있습니다.
    - `id`, `delivery_address`, `order_date_time`, `status`, `type`, `order_table_id` 필드로 구성되어있습니다.
    - `id`는 uuid로 부여/식별됩니다.
    - `delivery_address` 필드는 **DELIVERY(배달)** 주문 타입일 때 필수입니다. **TAKEOUT(포장)**, **EAT_IN(매장식사)** 주문 타입일 때는 무시됩니다.
    - `order_date_time` 필드는 주문 생성 시간입니다.
    - `status` 필드는 주문 상태입니다.   **WAITING**, **ACCEPTED**, **SERVED**, **DELIVERING**, **DELIVERED**, **COMPLETED** 중
      하나입니다.
        - 최초 생성 시 `status` 필드는 **WAITING**으로 설정됩니다.
    - `type` 필드는 주문 타입입니다. **DELIVERY(배달)**, **TAKEOUT(포장)**, **EAT_IN(매장식사)** 중 하나입니다.
    - `order_table_id` 필드는 **EAT_IN(매장식사)** 주문 타입일 때 필수입니다. **DELIVERY(배달)**, **TAKEOUT(포장)** 주문 타입일 때는 무시됩니다.
    - 요청 시 하나 이상의 `order_line_items`를 함께 등록해야합니다. `order_line_items` 상세한 내용은 아래에 설명합니다.
- 하위 요청을 통해 주문의 상태를 변경할 수 있습니다.
    - `PUT /api/orders/${orderId}/accept` : 주문을 접수합니다.
    - `PUT /api/orders/${orderId}/serve` : 주문을 제공합니다.
    - `PUT /api/orders/${orderId}/start-delivery` : 배달을 시작합니다.
    - `PUT /api/orders/${orderId}/complete-delivery` : 배달을 완료합니다.
    - `PUT /api/orders/${orderId}/complete` : 주문을 완료합니다.
- `GET /api/orders` 요청을 통해 주문 목록을 조회할 수 있습니다
