# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항
- 가게의 상품, 메뉴, 주문테이블을 관리할 수 있고 사용자가 음식을 주문할 수 있는 어플리케이션
- 상품
  - [ ] 상품을 생성을 한다
  - [ ] 모든 상품의 목록을 조회한다
  - [ ] 상품의 가격과 이름은 필수
  - [ ] 상품의 가격을 변경한다
  - [ ] 상품의 가격 변경으로 인해 메뉴의 가격 보다 작을 경우 상품이 포함된 메뉴는 전시 종료된다
  - [ ] 상품의 이름은 욕설/비속어가 포함되면 안된다
  - [ ] 상품의 가격은 0보다 커야한다

- 메뉴 상품 
  - [ ] 반드시 한개의 상품을 가지고 있어야한다
  - [ ] 상품의 재고량이 존재하며 0보다 커야한다
  - [ ] 특정 메뉴에 반드시 포함되어야 한다

- 메뉴
  - [ ] 메뉴를 생성한다
  - [ ] 메뉴의 가격을 변경한다
  - [ ] 메뉴의 전시 상태를 종료한다
  - [ ] 메뉴의 전시 상태로 활성화한다
  - [ ] 메뉴의 가격 및 이름은 필수
  - [ ] 메뉴의 이름은 욕설/비속어가 포함되면 안된다
  - [ ] 메뉴는 반드시 특정 메뉴 그룹에 포함이 되어야한다
  - [ ] 메뉴의 메뉴상품은 반드시 1개 이상 가지고 있어야한다 
  - [ ] 메뉴의 가격은 메뉴에 포함된 각각의 (메뉴 상품의 가격 * 메뉴 상품의 재고 수)의 합 보다 작거나 같아야하며 0보다 커야한다
    - [ ] 위의 정책은 메뉴의 전시상태 활성화로 변경 요청시에도 적용된다
    - [ ] 단, 상품의 가격이 변경이 될 경우에는 위의 규칙에 맞지 않는 메뉴들은 모두 전시상태가 종료된다

- 메뉴 그룹
  - [ ] 메뉴 그룹을 생성한다
  - [ ] 메뉴그룹의 이름은 필수
  - [ ] 모든 메뉴그룹을 조회한다
  - [ ] 0개 이상의 메뉴를 가지고 있을 수 있다

- 주문
  - [ ] 주문은 [ 배달(DELIVERY), 포장(TAKEOUT), 매장식사(EAT_IN) ] 중 하나의 타입에 속한다
  - [ ] 하나의 주문에는 하나 이상의 주문 아이템이 포함되어야 한다
  - [ ] 하나의 주문에는 매장식사의 경우에만 주문테이블을 1개 포함한다
    - [ ] 매장 식사는 요청 주문 테이블이 미사용 중이여야 주문이 가능하다 (코드 상으로 잘못 구현된 부분 있는것같음)
  - [ ] 배달 주문의 경우 배송지가 필수로 주문요청에 포함되어야 주문이 가능하다
  - [ ] 배달 주문 시 주문을 받은 후 배송 요청까지 진행한다
  - [ ] 주문시점의 수량 및 가격 정보를 가지고 있어야 한다 
    - [ ] 메뉴/상품의 가격이 변동되더라도 주문의 가격에는 영향 끼쳐서는 안된다
  - [ ] 전시 중인 메뉴만 주문이 가능하다
  - [ ] 매장식사의 경우에만 주문상품의 수량이 0보다 작을 수 있다(?)
  - [ ] 주문 요청시 메뉴의 가격이 주문요청의 가격과 동일해야 주문이 가능하다
  - [ ] 매장식사의 주문 완료 시 해당 주문 테이블에 완료된 주문이 없을 경우에는 미점유로 상태가 변경된다
  - [ ] 주문 상태는 다음 중 하나다 WAITING, ACCEPTED, SERVED, DELIVERING, DELIVERED, COMPLETED
    - [ ] <img width="1020" alt="Screenshot 2024-05-07 at 8 44 43 PM" src="https://github.com/next-step/ddd-legacy/assets/124428341/b2d9af40-211d-443b-873d-6a5791d9c31a">
- 주문 테이블
  - [ ] 방문해서 식사하는(EAT_IN) 손님들을 지정하기 위한 매장식사 전용 테이블
  - [ ] 주문 테이블에는 손님의 수용가능 숫자와 사용 중인지 여부를 지정할 수 있다
  - [ ] 주문 테이블을 점유 중으로(occupied) 변경할 수 있다
  - [ ] 완료되지 않은 주문이 없을 경우에만 주문 테이블을 미점유로 변경할 수 있다
  - [ ] 주문 테이블에 주문이 없고 점유 중인 상태의 경우에만 좌석 수를 변경할 수 있다


## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
