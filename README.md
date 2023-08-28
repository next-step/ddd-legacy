# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항

- 가게에서 사용할 포스 시스템을 구현한다.
- 상품 (Product)
  - [X] 새로운 상품을 등록한다.
    - [X] 상품에 대한 가격은 0원 이상이어야 한다.
    - [X] 상품명은 비어있을 수 없고, 255자를 초과할 수 없다.
    - [X] 상품명에 비속어가 포함되어 있으면 안 된다.
  - [X] 기존 상품의 가격을 변경한다.
    - [X] 상품에 대한 가격은 0원 이상이어야 한다.
    - [X] 해당 상품이 등록된 모든 메뉴에 대해서, 변경된 상품 가격을 토대로 메뉴의 가격 조건을 검증한다. 만약 조건을 만족하지 못 했을 때는, 해당 메뉴는 숨김 처리한다.
      (메뉴 가격 조건: 메뉴의 가격은 메뉴에 등록된 상품들의 가격과 수량을 곱한 값의 합보다 클 수 없다.)
  - [X] 모든 상품을 가져온다.
- 메뉴 그룹 (Menu Group)
  - [X] 새로운 메뉴 그룹을 등록한다.
    - [X] 메뉴 그룹명은 비어있을 수 없고, 255자를 초과할 수 없다.
  - [X] 모든 메뉴 그룹을 가져온다.
- 메뉴 (Menu)
  - [X] 새로운 메뉴를 등록한다.
    - [X] 메뉴 가격은 0원 이상이어야 한다.
    - [X] 등록할 메뉴 그룹이 있어야 한다.
    - [X] 메뉴에 등록할 상품이 1개 이상 있어야 한다.
    - [X] 메뉴에 등록할 상품의 수량은 0개 이상이어야 한다.
    - [X] 메뉴의 가격은 메뉴에 등록된 상품들의 가격과 수량을 곱한 값의 합보다 클 수 없다.
    - [X] 메뉴명은 비어있을 수 없고, 255자를 초과할 수 없다.
    - [X] 메뉴명에는 비속어가 포함되어 있으면 안 된다.
  - [X] 기존 메뉴의 가격을 변경한다.
    - [X] 메뉴 가격은 0원 이상이어야 한다.
    - [X] 메뉴의 가격은 메뉴에 등록된 상품들의 가격과 수량을 곱한 값의 합보다 클 수 없다.
  - [X] 등록한 메뉴의 상품들을 노출한다.
    - [X] 메뉴의 가격은 메뉴에 등록된 상품들의 가격과 수량을 곱한 값의 합보다 클 수 없다.
  - [X] 노출한 메뉴를 숨긴다.
  - [X] 모든 메뉴를 가져온다.
- 주문 테이블 (Order Table)
  - [X] 새로운 테이블을 등록한다.
    - [X] 테이블명은 비어있을 수 없고, 255자를 초과할 수 없다.
  - [X] 등록된 테이블에 고객을 입장시킨다.
  - [ ] 등록된 테이블에 고객을 퇴장시킨다.
    - [ ] 해당 테이블의 주문 상태가 완료가 아니면, 고객을 퇴장시킬 수 없다.
  - [X] 입장된 테이블의 고객 수를 변경한다.
    - [X] 테이블의 고객 수는 0명 이상이어야 한다.
    - [X] 테이블에 고객이 입장해 있는 상태여야 한다.
  - [X] 모든 테이블을 가져온다.
- 주문 (Orders)
  - 주문 유형에는 배달(Delivery), 포장(Takeout), 매장 식사(Eat in)가 있다.
  - 주문 상태에는 대기(Waiting), 승낙(Accepted), 제공(Served), 배달중(Delivering), 배달 완료(Delivered), 완료(Completed)가 있다
  - [X] 새로운 주문을 등록한다.
    - [X] 주문 유형은 비어있을 수 없다.
    - [X] 주문할 메뉴는 1개 이상 있어야 한다.
    - [X] 주문 유형이 매장 식사가 아닌 경우, 주문할 메뉴의 수량은 0개 이상이어야 한다.
    - [X] 주문할 메뉴가 노출된 상태이여야 한다.
    - [X] 주문할 메뉴의 가격은 메뉴에 등록된 가격과 같아야 한다.
    - [X] 주문 유형이 배달인 경우, 배달 주소가 있어야 한다.
    - [X] 주문 유형이 매장 식사인 경우, 주문 테이블에 입장해 있어야 한다.
  - [ ] 들어온 주문을 승낙한다.
    - [ ] 주문 상태가 대기 상태이여야 한다.
    - [ ] 주문 유형이 배달이면, 배달 업체에 배달 요청을 한다.
      - [ ] 주문 식별자, 메뉴에 등록된 상품들의 가격과 수량을 곱한 값의 합, 배달 주소를 배달 업체에 전달한다.
  - [ ] 승낙한 주문을 제공한다.
    - [ ] 주문 상태가 승낙 상태이여야 한다.
  - [ ] 주문에 대한 배달을 시작한다.
    - [ ] 주문 유형이 배달이여야 한다.
    - [ ] 주문 상태가 제공 상태이여야 한다.
  - [ ] 주문에 대한 배달을 완료한다.
    - [ ] 주문 상태가 배달중이여야 한다.
  - [ ] 주문을 완료한다.
    - [ ] 주문 유형이 배달이면, 주문 상태가 배달 완료이여야 한다.
    - [ ] 주문 유형이 포장 또는 매장 식사이면, 주문 상태가 제공이여야 한다.
    - [ ] 주문 유형이 매장 식사이면, 주문 테이블을 퇴장처리한다.
  - [ ] 모든 주문을 가져온다.

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
