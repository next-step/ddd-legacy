# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항

- 상품
  - [ ] 상품을 등록할 수 있다.
  - [ ] 상품은 가격과 이름을 반드시 가지고 있어야 한다.
  - [ ] 상품의 이름에 욕설이 포함되면 안 된다.
  - [ ] 상품의 가격을 변경할 수 있다.
  - [ ] 상품의 가격은 양수여야 한다.
  - [ ] 등록된 상품의 전체 목록을 볼 수 있다.

- 메뉴 그룹
  - [ ] 메뉴 그룹을 등록할 수 있다.
  - [ ] 메뉴 그룹은 이름을 반드시 가지고 있어야 한다.
  - [ ] 등록된 메뉴 그룹의 전체 목록을 볼 수 있다.

- 메뉴
  - [ ] 메뉴를 생성할 수 있다.
  - [ ] 메뉴는 이름, 가격, 메뉴 그룹과 상품들을 반드시 가지고 있어야 한다.
  - [ ] 메뉴의 가격은 양수여야 한다.
  - [ ] 메뉴 내부의 상품들의 총 가격보다 설정한 가격이 더 클 수 없다.
  - [ ] 메뉴에 등록된 상품의 가격이 변동되어 설정한 가격보다 더 커질 경우, 메뉴는 숨겨진다.
  - [ ] 메뉴 이름에 욕설이 포함되면 안 된다.
  - [ ] 메뉴의 가격을 변경할 수 있다.
  - [ ] 메뉴를 보이게 할 수 있다.
  - [ ] 메뉴를 안보이게 할 수 있다.
  - [ ] 메뉴의 전체 목록을 볼 수 있다.

- 주문
  - [ ] 주문을 생성할 수 있다.
  - [ ] 주문은 타입, 주문 품목을 반드시 가지고 있어야 한다.
  - [ ] 보여지는 메뉴만 주문할 수 있다.
  - [ ] 주문 대기이어야만 주문 수락으로 변경이 가능하다.
  - [ ] 주문 수락 시 배달해야 된다면 배달도 같이 요청한다.
  - [ ] 주문 수락 상태여야만 제공 상태로 변경이 가능하다.
  - [ ] 제공 상태여야만 배달 중 상태로 변경이 가능하다.
  - [ ] 배달해야 하는 주문만 배달 중 상태로 변경이 가능하다.
  - [ ] 배달 중 상태여야만 배달 완료 상태로 변경이 가능하다.
  - [ ] 주문 품목의 수량은 양수여야 한다.
  - [ ] 먹고 가기면 테이블을 잡고 있어야 주문이 가능하다.
  - [ ] 배달해야 하는 주문이 배달 완료 상태일 때만 주문 완료 상태로 변경이 가능하다.
  - [ ] 포장 또는 먹고 가기 주문이 제공 상태일 때만 주문 완료 상태로 변경이 가능하다.
  - [ ] 먹고 가기의 주문이 완료된다면 테이블을 비운다.
  - [ ] 주문의 전체 목록을 볼 수 있다.

- 테이블
  - [ ] 테이블을 생성할 수 있다.
  - [ ] 테이블은 이름을 반드시 가지고 있어야 한다.
  - [ ] 테이블에 앉을 수 있고, 해당 테이블은 점유 상태가 된다.
  - [ ] 테이블이 포함되어 있는 주문이 완료되었을 때만 테이블을 비울 수 있다.
  - [ ] 테이블을 비우면 테이블의 손님은 0명으로 설정되며 비점유 상태가 된다.
  - [ ] 점유 중인 테이블의 손님 수만 변경할 수 있다.
  - [ ] 테이블의 손님 수 변경 시 양수로만 변경 가능하다.
  - [ ] 테이블의 전체 목록을 볼 수 있다.

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
