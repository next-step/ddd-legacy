# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항

- 키오스크를 통한 메뉴 주문을 구현한다.
- 연관 메뉴
    - 사용자에게 판매 될 메뉴를 연관되어 전시하는것 입니다.
    - [X] 연관 메뉴는 이름으로 구성되어 있다.
    - [X] 연관 메뉴는 등록이 가능하다.
        - [X] 연관 메뉴는 이름으로 등록이 가능하며, 공백이면 안된다.
    - [X] 연관 메뉴는 전체 조회가 가능하다.
- 메뉴
    - 사용자가 최소 주문할 수 있는 단위 입니다.
    - [ ] 메뉴는 이름, 가격, 표출 여부, 연관 메뉴, 여러 메뉴 구성되어 있다.
    - [ ] 메뉴는 등록이 가능하다.
        - [ ] 메뉴의 이름은 공백이면 안되고, 비속어가 포함되면 안된다.
        - [ ] 메뉴의 가격은 0원 이상이어야 한다.
        - [ ] 메뉴는 연관 메뉴에 필수적으로 등록 되어야 한다.
        - [ ] 메뉴는 하나 이상의 상품으로 구성이 되어야 한다.
        - [ ] 메뉴의 가격은 모든 메뉴 구성의 합보다 크면 안된다.
    - [ ] 메뉴는 가격 수정이 가능하다.
        - [ ] 메뉴의 가격은 0원 이상이어야 한다.
    - [ ] 메뉴는 키오스크에 표출할 수 있다.
        - [ ] 메뉴의 가격은 0원 이상이어야 한다.
    - [ ] 메뉴는 키오스크에 미표출할 수 있다.
    - [ ] 메뉴는 전체 조회가 가능하다.
  - 메뉴 구성
      - 메뉴에 구성되는 상품 목록입니다.
      - [ ] 메뉴 구성은 상품과 상품 수량으로 구성되어 있다.
      - [ ] 메뉴 구성은 메뉴가 등록 될 때 함께 등록 된다.
- 상품
    - 메뉴를 구성하는 단품 입니다.
    - [X] 상품은 이름과 가격으로 구성되어 있다.
    - [X] 상품은 등록이 가능하다.
        - [X] 상품의 이름이 비어 있으면 안되고, 비속어가 포함되면 안된다.
        - [X] 상품의 가격은 0원 이상이어야 한다.
    - [X] 상품은 가격 수정이 가능하다.
        - [ ] 상품 가격이 수정 되었을 때 해당 상품을 포함하여 판매중인 메뉴의 가격이 더 비싸질 경우 해당 메뉴는 미표출 한다.
    - [X] 상품은 전체 조회가 가능하다.
- 주문
    - 고객이 메뉴를 구매하기 위한 요청입니다.
    - [ ] 주문은 수령 방법, 상태, 여러개의 주문 목록으로 구성되어 있다.
    - [ ] 주문 요청이 가능하다.
        - [ ] 주문의 수령 방법은 필수이다.
        - [ ] 주문의 주문 목록은 필수이다.
        - [ ] 주문 수령 방법이 배달일 경우 배달지 주소는 필수이다.
        - [ ] 주문 수령 방법이 매장내 식사의 경우 테이블은 필수이다.
        - [ ] 매장 식사의 경우가 아닐 경우 주문 목록의 메뉴 수랑은 필수 이다.
        - [ ] 표출된 메뉴만 주문 요청이 가능하다.
        - [ ] 주문 요청시의 가격과 현재 메뉴 가격은 같아야 한다.
    - [ ] 주문 승인이 가능하다.
        - [ ] 승인 될 주문은 주문 요청이 된 상태여야 한다.
        - [ ] 주문 수령 방법이 배달일 경우 라이더 배정은 필수이다.
    - [ ] 주문 제공이 가능하다.
        - [ ] 제공 될 주문은 주문 승인이 된 상태여야 한다.
    - [ ] 주문 배달이 가능하다.
        - [ ] 주문 수령 방법이 배달 이어야 한다.
        - [ ] 배달 될 주문은 주문 제공이 된 상태여야 한다.
    - [ ] 주문 배달 완료가 가능하다.
        - [ ] 주문 수령 방법이 배달 이어야 한다.
        - [ ] 배달 완료 될 주문은 배달중 상태여야 한다.
    - [ ] 주문 종료가 가능하다.
        - [ ] 주문 수령 방법이 배달의 경우 상태는 배달됨 이어야 한다.
        - [ ] 테이크 아웃 또는 매장내 식사의 경우 상태는 제공됨 상태이어야 한다.
        - [ ] 매장내 식사의 경우 테이블을 청소한다.
    - [ ] 주문은 전체 조회가 가능하다.
    - 주문 목록
      - 주문은 여러개의 메뉴로 구성될 수 있으며, 이것을 주문 목록이라고 합니다.
        - [ ] 주문 목록은 메뉴와 메뉴 수량으로 구성되어 있다.
        - [ ] 주문 목록은 주문 요청이 될 때 같이 등록 된다.
            - [ ] 주문 목록의 총 가격은 메뉴와 메뉴 수량으로 계산한다.
- 테이블
    - [ ] 매장 내의 식사를 하시는 분들을 위한 테이블입니다.
    - [ ] 테이블은 이름과 인원수, 착석 여부로 구성되어 있다.
    - [ ] 테이블은 등록이 가능하다.
        - [ ] 테이블의 이름은 공백이면 안된다.
    - [ ] 테이블은 착석이 가능하다.
        - [ ] 착석시 테이블은 착석으로 변경한다.
    - [ ] 테이블은 청소가 가능하다.
        - [ ] 테이블을 청소할 때는 테이블에 있던 주문은 종료 상태여야 한다.
        - [ ] 청소시 좌석을 비우고, 미착석으로 변경한다.
    - [ ] 테이블은 인원수를 수정할 수 있다.
        - [ ] 인원수는 0명 이상이어야 한다.
        - [ ] 착석중인 테이블이어야 한다.
    - [ ] 테이블은 전체 조회가 가능하다.

## 용어 사전

## 모델링
