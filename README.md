# 키친포스
## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항

### 상품
- [x] 상품은 이름, 가격을 갖는다
  - [x] 상품의 이름은 공백, 빈문자열을 허용하며 반드시 존재한다
  - [ ] 상품의 이름은 욕설이 포함될 수 없다
  - [x] 상품의 가격은 0원 이하가 될 수 없다
  - [x] 상품의 가격은 변경될 수 있다
    - [x] 상품의 가격을 변경시 메뉴 가격이 메뉴 상품의 (가격 * 수량) 값보다 크면 메뉴를 숨김 처리한다

### 메뉴 상품
- 메뉴 상품은 상품의 수량을 관리한다
  - 수량은 최소 0개 이상 이어야 한다
  - 메뉴 상품의 상품 가격과 수량을 곱하면 메뉴의 가격이다

### 메뉴
- [x] 메뉴는 이름, 가격, 표시 여부를 갖는다
- [x] 메뉴의 이름은 한 글자 이상 존재해야 한다
- [ ] 메뉴의 이름은 욕설이 포함될 수 없다
- [x] 메뉴의 가격은 0원 초과여야 한다
- [x] 메뉴는 하나의 메뉴그룹에 소속된다
- [x] 메뉴 가격을 변경시 메뉴 가격이 메뉴 상품의 (가격 * 수량) 값보다 크면 가격 변경이 불가능하다
- [x] 메뉴는 보임, 숨김 처리가 가능하다
  - [ ] 메뉴 가격이 메뉴 상품의 (가격 * 수량) 값보다 크면 메뉴 보임 처리할 수 없다
  - [ ] 메뉴 숨김은 메뉴가 존재하는 한 언제든지 숨김처리 가능하다



### 주문
- 주문 생성
  - 주문의 타입은 배달, 포장, 매장 식사 중 하나여야 한다 
    - 주문 타입이 없을 수는 없다
  - 배달 주문 
    - 배달 주문은 주소 정보가 있어야 한다
  - 매장 식사 주문
    - 주문 테이블 정보가 있어야 한다 
    - 주문 수량이 0보다 작을 수 없다
  - 주문 상태는 *대기, 접수, 제공, 배달중, 배달완료, 주문완료*가 있다
    - 배달 주문은 *대기, 접수, 제공, 배달중, 배달완료, 주문완료* 순으로 상태가 변한다
    - 매장 식사 주문은 *대기, 접수, 제공, 주문완료* 순으로 상태가 변한다 
    - 포장 주문은 *대기, 접수, 제공, 주문완료* 순으로 상태가 변한다
    - 주문의 최초 상태는 대기 이다
  - 주문은 여러 개의 주문한 메뉴를 갖는다
    - 주문은 메뉴를 최소 1개 갖는다
    - 주문한 메뉴는 등록되어 있는 메뉴여야 한다
    - 감추기된 메뉴가 주문될 수 없다
    - 주문한 메뉴의 가격은 0보다 작을 수 없다
- 주문 접수
  - 대기 상태의 주문만 접수가 가능하다
  - 주문 타입이 배달이라면 라이더에게 배달 요청을 한다
  - 주문 수락 후 주문 상태가 접수로 변경된다
- 주문 제공
  - 접수 상태의 주문만 제공으로 변경될 수 있다
- 주문 배송 시작
  - 주문 타입이 배달이고 상태가 제공일때만 배달중 상태로 변경 가능하다 
- 주문 배송 완료
  - 주문 상태가 배달중인 주문만 배달완료로 변경 가능하다
- 주문 완료
  - 주문 타입이 DELIVERY이면 주문 상태가 DELIVERED일 때만 완료로 변경 가능하다
  - 주문 타입이 TAKEOUT 또는 EAT_IN이면 상태가 SERVED일때만 완료로 변경 가능하다
  - 주문 타입이 EAT_IN이면 주문 테이블의 인원 수는 0, 테이블을 공석으로 변경한다

### 주문 테이블
- 주문 테이블은 이름, 인원 수, 공석 여부를 갖는다
- 주문 테이블의 초기 상태는 인원 수는 0이며 공석이다
- 주문 테이블을 초기화 한다
  - 초기화는 주문테이블의 인원 수는 0, 상태는 공석이 된다
  - 주문 테이블에 존재하는 주문의 상태가 완료라면 초기화할 수 없다
- 주문 테이블의 인원수는 변경 가능하다
  - 변경할 인원 수가 0이라면 변경 불가능하다
  - 주문 테이블이 공석 상태라면 불가능하다

## 용어 사전

| 한글명     | 영문명          | 설명                    |
|---------|--------------|-----------------------|
| 메뉴      | menu         | 상품과 가격으로 구성된 메뉴 정보    |
| 메뉴 그룹   | menu group   | 메뉴를 그룹핑하는 상위 개념       |
| 상품      | product      | 상품명과 가격으로 구성된다        |
| 메뉴 상품   | menu product | 상품과 상품의 수량을 관리하는 개념   |
| 주문      | order        | 고객의 주문 정보             |
| 주문 타입   | order type   | 주문의 유형                |
| 딜리버리    | DELIVERY     | 배달 주문                 |
| 테이크아웃   | TAKEOUT      | 포장 주문                 |
| 매장 내 식사 | EAT_IN       | 매장 내 식사 주문            |
| 주문 테이블  | order table  | 매장 내 식사인 경우 주문을 한 테이블 |
| 주문 상태   | order status | 주문의 상태                |
| 대기      | WAITING      | 주문 대기                 |
| 승낙      | ACCEPTED     | 주문 접수                 |
| 제공      | SERVED       | 주문한 메뉴가 제공된 상태        |
| 배달      | DELIVERING   | 배달 주문인 경우 배달중 상태      |
| 배달완료    | DELIVERED    | 배달 주문인 경우 배달완료 상태     |
| 완료      | COMPLETED    | 주문이 완료된 상태            |
## 모델링
