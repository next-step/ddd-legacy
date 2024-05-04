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
- 그룹의 아이디는 uuid로 식별됩니다.
- `POST /api/menu-groups` 요청을 통해 메뉴 그룹을 등록 할 수 있습니다.
    - **name** 필드는 필수입니다.
- `GET /api/menu-groups` 요청을 통해 메뉴 그룹 목록을 조회 할 수 있습니다.

### Product

- 메뉴는 하나 이상의 상품으로 구성될 수 있습니다.
    - 예를 들어 `후라이드 치킨 세트` **메뉴**에는 `후라이드 치킨`과 `콜라`가 포함될 수 있습니다.
- 상품의 아이디는 uuid로 식별됩니다.
- `POST /api/products` 요청을 통해 상품을 등록 할 수 있습니다.
    - **name** 과 **price** 필드는 필수입니다.
- `PUT /api/products/${productId}/price` 요청을 통해 상품의 가격을 수정 할 수 있습니다.
    - 경로의 **${productId}** 는 상품의 아이디 입니다.
    - **price** 필드는 필수입니다.
    - **price** 필드는 0보다 큰 값이어야 합니다.
- `GET /api/products` 요청을 통해 상품 목록을 조회 할 수 있습니다.

## Order Aggregate
