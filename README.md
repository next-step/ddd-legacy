# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항
- 키친포스를 구현한다
- 키친포스는 카테고리 (MenuGroup), 메뉴 (Menu), 주문 (Order), 테이블 (OrderTable), 상품(Product)로 이루어져 있다.
- 카테고리 (MenuGroup)
  - [ ] 카테고리는 아이디와 이름으로 구성되어 있다.
  - [ ] 카테고리 아이디는 UUID로 되어있다.
  - [ ] 카테고리를 조회할 수 있다.
  - [ ] 카테고리를 생성할 수 있다.
      - [ ] 이름이 없거나, 이름이 빈칸이면 예외를 발생시킨다.
- 메뉴 (Menu)
- 주문 (Order)
  - [ ] 주문은 아이디, 주문 타입, 주문 상태, 주문 시간, 주문한 상품 정보, 주소, 주문 아이디로 이루어져 있다.
  - [ ] 주문 아이디는 UUID로 이루어져 있다.
  - [ ] 주문 타입은 배달, 테이크 아웃, 안에서 먹기가 있다.
  - [ ] 주문 상태는 대기 중, 주문 받기 완료, 주문 서빙 완료, 배달 중, 배달 완료, 주문 처리 완료가 있다.
  - [ ] 주문 목록을 조회할 수 있다.
  - [ ] 배달 주문 시작을 할 수가 있다.
      - [ ] 주문 아이디가 존재하지 않으면 예외를 발생시킨다.
      - [ ] 주문 타입이 배달이 아닌 경우 예외를 발생시킨다.
      - [ ] 주문 상태가 주문 받기 완료 상태가 아니면 예외를 발생시킨다.
  - [ ] 주문에 대해 배달을 완료할 수 있다.
      - [ ] 주문 아이디가 존재하지 않으면 예외를 발생시킨다.
      - [ ] 주문 상태가 배달 중이 아니라면 예외를 발생시킨다.
  - [ ] 주문을 완료할 수 있다.
      - [ ] 주문 아이디가 존재하지 않으면 예외를 발생시킨다.
      - [ ] 주문 타입이 배달인 경우, 주문 상태가 배달 완료가 아닌 경우에는 예외를 발생시킨다.
      - [ ] 주문 타입이 가져가기인 경우 주문 받기 완료 상태가 아니면 예외를 발생시킨다.
      - [ ] 주문 타입이 먹고가기인 경우 주문 받기 완료 상태가 아니면 예외를 발생시킨다.
      - [ ] 주문 타입이 먹고가기인 경우 주문 상태에 대해 주문 테이블 정보가 있고 주문 완료 상태가 완료가 아닌 경우 주문 테이블 정보 중 인원을 0명으로 만들고 비어있도록 상태를 변경한다.
  - [ ] 주문을 받을 수 있다.
      - [ ] 주문 아이디가 존재하지 않으면 예외를 발생시킨다.
      - [ ] 주문 상태을 주문 받기 완료가 아니면 예외를 발생시킨다.
  - [ ] 주문을 수락할 수 있다.
      - [ ] 주문 상태가 기다리는 중이 아니라면 예외를 발생시킨다.
      - [ ] 주문 상태가 배달인 경우 배달정보(주문 번호, 가격, 주소)를 배달 기사에게 전달한다.
  - [ ] 주문을 할 수 있다.
      - [ ] 주문 타입이 없다면 예외를 발생시킨다.
      - [ ] 주문한 메뉴가 없거나, 비어있다면 예외를 발생시킨다.
      - [ ] 주문한 메뉴가 메뉴 목록에 없다면 예외를 발생시킨다.
      - [ ] 배달, 테이크 아웃인 경우 주문한 수량이 0 인 경우 예외를 발생시킨다.
      - [ ] 주문한 메뉴가 비공개 되어있다면 예외를 발생시킨다.
      - [ ] 주문한 메뉴의 가격과 등록 된 메뉴의 가격이 다른 경우 예외를 발생시킨다.
      - [ ] 주문한 경우 초기 값은 대기 중으로 되어진다.
      - [ ] 주문 타입이 배달인 경우 주소가 없거나, 빈칸이면 예외를 발생시킨다.
      - [ ] 주문 타입이 안에서 먹기인 경우 테이블 아이디가 없는 테이블 아이디면 예외를 발생시킨다.
      - [ ] 주문 타입이 안에서 먹기인 경우 테이블이 비어있다면 예외를 발생시킨다.
- 테이블 (OrderTable)
  - [ ] 테이블은 아이디, 이름, 손님 숫자, 착석 유무로 이루어져 있다.
  - [ ] 테이블 목록을 조회할 수 있다.
  - [ ] 테이블을 추가할 수 있다.
      - [ ] 이름이 없거나, 이름에 빈값이 들어가 있으면 예외를 발생시킨다.
  - [ ] 테이블에 착석할 수 있다.
      - [ ] 주문 테이블 아이디에 대한 주문 테이블이 존재하지 않으면 예외를 발생시킨다.
  - [ ] 테이블을 비울 수 있다.
      - [ ] 주문 테이블 아이디에 대한 주문 테이블이 존재하지 않으면 예외를 발생시킨다.
      - [ ] 주문 테이블에 주문이 있고 주문의 상태가 완료된 상태가 아니라면 예외를 발생시킨다.
  - [ ] 테이블에 앉은 손님의 수를 변경할 수 있다.
      - [ ] 손님의 수가 0 이하면 예외를 발생시킨다.
      - [ ] 주문 테이블 아이디에 대한 주문 테이블이 존재하지 않으면 예외를 발생시킨다.
      - [ ] 주문 테이블이 착석되지 않은 상태면 예외를 발생시킨다.
- 상품 (Product)
  - [ ] 상품은 아이디, 이름, 가격으로 구성되어 있다.
  - [ ] 상품의 아이디는 UUID로 되어있다.
  - [ ] 상품 목록을 조회할 수 있다.
  - [ ] 상품 아이디를 통하여 상품 가격을 변경할 수 있다.
      - [ ] 가격이 없거나, 가격이 0원 이하이면 예외를 발생시킨다.
      - [ ] 상품 아이디에 대한 상품이 존재하지 않으면 예외를 발생시킨다.
      - [ ] 상품이 메뉴에 등록되어 있다면 메뉴에 등록 된 상품의 가격과 수량을 곱한 값이 상품의 가격보다 낮은 경우 메뉴를 숨긴다.
  - [ ] 상품을 추가할 수 있다.
      - [ ] 상품을 추가하므로써 아이디, 이름, 가격을 넣을 수 있다.
      - [ ] 가격이 없거나, 가격이 0원 이하이면 예외를 발생시킨다.
      - [ ] 이름이 없거나, 이름에 비속어가 들어가 있으면 예외를 발생시킨다.



## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
