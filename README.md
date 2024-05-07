# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항

이 애플리케이션은 식당이나 카페 등의 음식 서비스 업체에서 사용할 수 있는 주문 관리 시스템을 구현합니다.

### 메뉴 관리

- **메뉴 그룹**
    - 메뉴 그룹은 식당이나 카페에서 제공하는 다양한 메뉴들을 특정 카테고리로 분류하기 위한 단위입니다.
        - 예를 들어, '아침 메뉴', '점심 특선', '디저트' 등으로 메뉴를 그룹화하여 고객이 원하는 음식을 쉽게
          찾을 수 있도록 합니다.
    - [ ] 메뉴 그룹을 생성할 수 있습니다. 각 메뉴 그룹은 고유한 이름을 가지고 있으며, 이름은 중복될 수 없습니다.
    - [ ] 모든 메뉴 그룹을 조회할 수 있습니다.

- **메뉴**
    - [ ] 새로운 메뉴를 생성할 수 있습니다.
        - [ ] 메뉴 생성 시 상품 목록을 지정해야 합니다.
        - [ ] 메뉴 생성 시 비속어가 포함되어 있지 않은 이름을 지정해야 합니다.
        - [ ] 메뉴 생성 시 0 이상 각 상품 목록들의 가격의 합 이하의 금액을 가격으로 지정해야 합니다.
    - [ ] 메뉴의 가격을 변경할 수 있습니다. 단 메뉴의 가격은 0 이상 각 상품 목록들의 가격의 합 이하의 금액이어야 합니다.
    - [ ] 메뉴를 화면에 표시하거나 숨길 수 있습니다.
    - [ ] 모든 메뉴를 조회할 수 있습니다.

### 주문 관리

- [ ] 새로운 주문을 생성할 수 있습니다.
    - [ ] 주문 생성 시 주문의 종류를 지정해야 합니다.
        - [ ] 주문의 종류에는 **포장**, **배달**, **매장 식사** 가 있습니다.
    - [ ] 주문 생성 시 메뉴 목록과 각 메뉴 목록의 주문 수량을 지정해야 합니다.
    - [ ] 주문 생성 시 숨겨진 메뉴는 지정할 수 없습니다. 표시된 메뉴만 지정할 수 있습니다.
- [ ] **매장 식사** 주문은 **수락 대기** -> **수락** -> **서빙** -> **주문 완료** 의 순으로 주문 상태가 변경됩니다.
- [ ] **포장** 주문은 **수락 대기** -> **수락** -> **서빙** -> **주문 완료** 의 순으로 주문 상태가 변경됩니다.
- [ ] **배달** 주문은 **수락 대기** -> **수락** -> **서빙** -> **배달 시작** -> **배달 완료** -> **주문 완료** 의 순으로 주문 상태가 변경됩니다.
- [ ] 모든 주문을 조회할 수 있습니다.

### 주문 테이블 관리

- [ ] 새로운 주문 테이블을 생성할 수 있습니다.
- [ ] 주문 테이블의 사용 상태를 관리할 수 있습니다.
    - [ ] 주문 테이블에 손님이 앉을 때 테이블을 **사용 중**인 상태로 변경합니다.
    - [ ] 주문 테이블을 손님이 떠날 때, 테이블을 **사용 가능**한 상태로 변경합니다.
- [ ] 주문 테이블에 손님이 앉은 후, 앉은 손님의 수는 변경될 수 있습니다.
- [ ] 모든 주문 테이블을 조회할 수 있습니다.

### 상품 관리

- [ ] 새로운 상품을 등록할 수 있습니다.
    - [ ] 상품 등록 시 상품의 가격은 0 이상이어야 합니다.
    - [ ] 상품 등록 시 상품의 이름에는 비속어가 포함되지 않아야 합니다.
- [ ] 상품의 가격을 0 이상의 금액으로 변경할 수 있습니다.
    - [ ] 상품의 가격을 변경할 때, 해당 상품을 포함하는 모든 메뉴의 총 가격을 재계산합니다.
    - [ ] 변경된 상품의 가격과 수량을 곱하여 새로운 가격을 계산하고, 해당 상품을 포함하는 메뉴의 모든 상품 가격의 총합을 업데이트합니다.
    - [ ] 메뉴의 모든 상품 가격의 총합을 새로 계산한 후, 메뉴의 설정된 가격과 비교합니다.
        - [ ] 만약 메뉴의 설정된 가격이 새로운 총합 가격보다 크거나 같으면, 메뉴는 계속 표시됩니다.
        - [ ] 새로운 총합 가격이 메뉴의 설정된 가격보다 작을 경우, 메뉴는 표시되지 않게 설정됩니다.
- [ ] 모든 상품을 조회할 수 있습니다.

## 용어 사전

| 한글명 | 영문명 | 설명 |
|-----|-----|----|
|     |     |    |

## 모델링
