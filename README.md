# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항

### 상품
- [ ] 매장에서 판매할 상품을 의미한다.
- [ ] 상품은 이름, 가격 정보를 가진다.
- [ ] 상품을 등록한다.
  - [ ] 상품 이름은 필수로 부여되야하며 욕설이 포함된 상품명은 불가능하다.
  - [ ] 상품 가격은 0원보다 큰 금액이 할당되야 한다.
- [ ] 상품에 가격을 변경한다.
  - [ ] 상품에 가격정보가 변경될 경우 해당 상품으로 구성된 메뉴가격이   
        메뉴상품 총 가격(상품가격 * 갯수)보다 클경우 메뉴노출을 비활성화 해야한다.
- [ ] 전체 상품을 조회할 수 있다.

----

### 메뉴상품
- [ ] 메뉴를 구성하는 상품정보를 의미한다.
- [ ] 상품정보와 수량정보를 가진다.

----

### 메뉴그룹
- [ ] 여러 메뉴를 묶을 수 있는 개념을 의미한다.
- [ ] 메뉴그룹은 이름 정보를 가진다.
- [ ] 메뉴그룹을 등록한다.
  - [ ] 이름은 필수로 부여되야한다.
- [ ] 전체 메뉴그룹을 조회할 수 있다.

----

### 메뉴
- [ ] 고객에게 노출될 메뉴정보를 의미한다.
- [ ] 메뉴는 이름, 가격, 노출여부, 메뉴상품들, 메뉴그룹을 갖는다.
- [ ] 메뉴를 등록한다.
  - [ ] 가격은 0원보다 커야하며 메뉴를 구성하는 메뉴상품에 총 가격(상품가격 * 갯수)보다 클 수 없다.
  - [ ] 메뉴상품들에 각 상품에 수량은 0보다 커야한다.
  - [ ] 이름에 욕설이 포함된 메뉴명은 불가능하다.
  - [ ] 메뉴그룹에 소속된다.
- [ ] 메뉴가격을 변경한다.
  - [ ] 변경되는 메뉴 가격이 메뉴상품들에 총 가격(상품가격 * 갯수)보다 클 수 없다.
- [ ] 메뉴를 노출한다.
  - [ ] 메뉴가격이 메뉴상품들에 총 가격(상품가격 * 갯수)보다 크지 않을경우 메뉴를 노출할 수 있다.
- [ ] 메뉴는 임의로 비노출 할 수 있다.
- [ ] 전체 메뉴를 조회할 수 있다.

----

### 테이블
- [ ] 고객이 매장에서 식사를 할수있는 테이블을 의미한다.
- [ ] 테이블은 이름, 고객 수, 착석여부를 갖는다.
- [ ] 테이블을 등록한다.
  - [ ] 이름은 필수로 부여되야한다.
- [ ] 테이블에 착석한다.
  - [ ] 테이블에 착석 할 경우 테이블의 착석여부에 상태가 착석으로 변경된다.
- [ ] 테이블을 치운다.
  - [ ] 테이블을 치울경우 고객 수는 0으로 변경되며 착석여부는 비착석으로 변경된다.
  - [ ] 테이블에 착석 한 고객의 주문이 처리된 경우 테이블을 초기화 할 수 있다.
- [ ] 몇명인지 얘기한다.
  - [ ] 착석한 고객은 몇명인지 얘기할 수 있으나 음수를 얘기할 수 없다.
  - [ ] 착석하지 않은 고객은 몇명인지 얘기할 수 없다.
- [ ] 전체 테이블에 정보를 조회할 수 있다.

----

### 주문정보
- [ ] 주문시에 구성된 메뉴정보를 의미한다.
- [ ] 주문정보는 메뉴, 수량, 가격을 갖는다.

----

### 주문
- [ ] 주문은 고객이 메뉴를 주문한 정보를 의미한다.
- [ ] 주문은 주문타입, 주문상태, 주문일시, 주문정보, 배달주소, 테이블정보를 가진다.
- [ ] 주문타입은 배달주문, 포장주문, 매장주문이 존재한다
- [ ] 주문상태는 대기, 수락, 제공, 배달중, 배달완료, 완료가 존재한다.
- [ ] 주문한다.
  - [ ] 주문시 배달정보는 주문타입이 배달주문일경우 필수로 갖는다.
  - [ ] 주문시 착석된 테이블정보는 주문타입이 매장주문일경우 필수로 갖는다.
  - [ ] 주문시 매장식사가 아닐경우 주문정보에 수량이 0보다 작을 수 없다.
  - [ ] 주문시 주문정보에 메뉴가 비노출 상태이거나 가격이 메뉴가격과 다를 수 없다.
  - [ ] 주문시 주문상태는 대기상태이다.
  - [ ] 주문시 주문일시가 생성된다.
- [ ] 주문을 수락한다.
  - [ ] 주문상태가 대기상태일경우 주문을 수락할 수 있다.  
  - [ ] 주문수락시 주문타입이 배달 일 경우 주문번호, 총가격, 배달주소 정보로 배달요청을 한다.
- [ ] 주문한 메뉴를 제공한다.
  - [ ] 주문상태가 수락일경우 제공할 수 있다.
- [ ] 배달을 시작한다.
  - [ ] 주문타입이 배달이고 주문상태가 제공상태 일 경우 배달중 상태로 변경할 수 있다.
- [ ] 배달을 완료한다.
  - [ ] 주문상태가 배달중일경우 배달완료 상태로 변경할 수 있다.
- [ ] 주문을 완료한다.
  - [ ] 배달주문인 경우 배달완료 상태일경우 주문완료 상태로 변경할 수 있다.
  - [ ] 포장주문인 경우 제공 상태일경우 주문완료 상태로 변경할 수 있다.
  - [ ] 매장주문인 경우 제공 상태일경우 주문완료 상태로 변경할 수 있으며 해당 고객이 착석한 테이블을 치운다.

----

## 용어 사전

| 한글명  | 영문명           | 설명                       |
|------|---------------|--------------------------|
| | | |

## 모델링
