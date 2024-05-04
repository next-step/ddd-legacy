# 키친포스

## How to run
```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항

- 메뉴 애그리거트과 주문 애그리거트으로 구분 될 수 있습니다.

### Menu Aggregate

#### MenuGroup

- 메뉴는 하나 이상의 메뉴로 구성될 수 있습니다.
- 메뉴 그룹은 각각 고유한 걔념을 가질 수 있습니다.
    - ex. `추천메뉴` ,`순살파닭두마리메뉴` ,`한마리메뉴` ,`신메뉴` ,`두마리메뉴`
- POST `/api/menu-groups` 요청을 통해 메뉴 그룹을 등록 할 수 있습니다.
    - 그룹의 아이디는 uuid로 식별됩니다.
    - 'name' 필드는 필수입니다.
- GET `/api/menu-groups` 요청을 통해 메뉴 그룹 목록을 조회 할 수 있습니다.

### Order Aggregate
