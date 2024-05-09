# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항
- 식당 음식 판매 정보 관리 기기 구현하기
- 메뉴 그룹
  - 메뉴 그룹이란 메뉴들의 집합을 의미한다.
  - [X] 새로운 메뉴 그룹을 추가할 수 있다.
    - [X] 메뉴 그룹 추가 시 이름이 반드시 존재해야 한다.
  - [] 모든 메뉴 그룹을 조회할 수 있다.
- 메뉴
  - 메뉴란 식당에서 판매되고 있는 음식과 해당 음식에 관련된 정보들이다.
  - [X] 새로운 메뉴를 추가할 수 있다.
    - [X] 메뉴는 반드시 메뉴 그룹에 포함 되어야 한다.
    - [X] 메뉴는 반드시 가격, 이름, 상품 정보를 가지고 있어야 한다.
    - [] 메뉴의 상품 수량은 기존 상품의 수량과 같아야 한다.
    - [X] 메뉴의 가격은 상품 포함된 상품의 총 가격 보다 클 수 없다.
    - [X] 메뉴 추가 시 메뉴의 이름이 부적절한지 검사한다.
  - [X] 메뉴의 가격을 변경할 수 있다.
    - [X] 메뉴의 가격은 해당 상품 총 가격 보다 클 수 없다.
  - [] 메뉴를 노출할 수 있다.
  - [] 메뉴를 숨길 수 있다.
  - [] 모든 메뉴를 조회할 수 있다.
- 주문
  - 주문이란 식당에 있는 메뉴에 있는 음식에 주문한는 것을 의미한다.
  - [] 주문의 상태는 대기, 접수, 제공, 배달 중, 배달 완료, 완료가 존재한다.
  - [] 주문의 유형은 매장 식사, 포장, 배달이 존재한다.
    - [] 주문 시 주문 항목은 반드시 존재 해야한다. 
    - [] 주문 항목 개수는 메뉴의 개수와 일치해야 한다.
    - [] 주문 항목에 있는 메뉴들은 판매 되고 있는 메뉴들이어야 한다.
    - [] 주문 가격은 주문 항목에 있는 메뉴의 가격 총합과 같아야 한다.
    - [] 주문을 하게 되면 즉시 해당 주문의 상태는 주문 대기 상태가 된다.
  - [] 손님은 식당에서 음식을 주문할 수 있다.
    - [] 식당 주문 시 주문 항목에 반드시 수량이 존재해야 한다.
    - [] 식당 주문 시 자리가 존재해야 하며 앉을 수 있어야 한다.
    - [] 주문이 접수가 되면 손님에게 음식을 제공한다.
    - [] 손님에게 음식이 제공되면 주문 상태를 주문 완료로 변경한다.
    - [] 주문 상태가 주문 완료가 되면 매장 주인은 테이블을 정리한다.
  - [] 손님은 배달을 주문 할 수 있다.
    - [] 배달 주문 시 배달 주소지가 반드시 있어야 한다.
    - [] 배달 주문이 접수가 되면 라이더에게 주문 정보와 배달 주소시를 전달하며 배달을 요청한다.
      - [] 배달을 요청할 경우 해당 주문의 상태는 접수 상태로 변경된다.
    - [] 배달이 시작되면 주문의 상태를 배달 중으로 변경한다.
    - [] 배달이 완료되면 주문의 상태를 배달 완료로 변경한다.
    - [] 배달 완료가 확인되면 주문의 상태를 주문 상태를 주문 완료로 변경한다.
  - [] 손님은 포장 주문을 할 수 있다.
    - [] 주문이 접수가 되면 손님에게 음식을 제공한다.
    - [] 손님에게 음식이 제공되면 주문 상태를 주문 완료로 변경한다.
  - [] 주문을 했을 때 주문이 가능하다면 주문 접수를 진행한다.
    - [] 주문의 상태는 대기, 접수, 전달, 배달 중, 배달 완료, 완료를 가진다.
    - [] 주문을 하게 되면 가장 먼저 주문 대기 상태가 된 후 접수 상태로 변경된다.
  - [] 모든 주문을 조회할 수 있다.
- 테이블
  - 테이블이란 식당에서 식사할 수 있는 탁자를 의미한다.
  - [] 새로운 테이블을 추가할 수 있다.
    - [] 새로운 테이블을 추가 시 테이블의 이름은 반드시 존재해야 한다.
    - [] 추가된 테이블은 손님이 바로 식사 할 수 있다.
  - [] 손님은 테이블에 앉을 수 있다.
  - [] 매장 주인은 테이블을 정리할 수 있다.
    - [] 주문이 완료 된 상태의 테이블에서만 청소가 가능하다.
  - [] 테이블에 앉는 손님의 수는 변경될 수 있다.
    - [] 현재 테이블은 손님이 앉아있는 상태여야 한다.
    - [] 앉아 있는 손님은 1명 이상이어야 한다.
  - [] 모든 테이블을 조회 할 수 있다.
- 상품
  - 상품이란 메뉴에 구성되는 상품을 의미한다.
  - [X] 새로운 상품을 추가 할 수 있다.
    - [X] 상품의 가격은 반드시 존재해야 하며 0보다 커야 한다.
    - [X] 상품의 이름은 반드시 존재해야 하며 부적절한지 검사한다.
  - [] 상품의 가격을 변경할 수 있다.
    - [] 해당 상품으로 구성된 메뉴의 가격이 변경된 상품의 가격 총합보다 크다면 메뉴를 노출하지 않는다.
  - [] 모든 상품을 조회 할 수 있다.

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
