# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항

### 상품

#### 상품 등록
- [x] 상품의 가격은 필수 값이며 0보다 작으면 안된다.
- [x] 상품의 이름은 필수 값이다.
- [x] 상품의 이름에 비속어가 들어갈 수 없다.

#### 상품 금액 수정

- [x] 상품의 가격은 필수 값이며 0보다 작으면 안된다.
- [x] 해당 상품을 포함하는 메뉴의 가격이 메뉴에 포함 된 (상품 가격 * 상품 수량)의 합보다 크면 메뉴 노출이 비활성화 된다.

#### 상품 조회

- [x] 상품의 전체 목록을 조회한다.

### 메뉴

#### 메뉴 등록

- [x] 메뉴의 가격은 필수 값이며 0보다 작으면 안된다.
- [x] 메뉴는 메뉴 그룹에 속해야 한다.
- [x] 메뉴의 이름에 비속어가 들어갈 수 없다.
- [x] 메뉴 가격은 메뉴 상품의 총 가격(상품*수량)보다 클 수 없다.

#### 메뉴 가격 변경

- [x] 메뉴의 가격은 필수 값이며 0보다 작으면 안된다.
- [x] 메뉴 가격은 메뉴 상품의 총 가격(상품*수량)보다 클 수 없다.

#### 메뉴 노출 설정

- [x] 메뉴 가격은 메뉴 상품의 총 가격(상품*수량)보다 클 수 없다.

#### 메뉴 숨김 설정

- [x] 메뉴를 숨김 설정할 수 있다.

#### 메뉴 조회

- [x] 메뉴의 전체 목록을 조회한다.

### 메뉴 그룹

#### 메뉴 그룹 등록

- [x] 메뉴 그룹의 이름은 필수값이다.

#### 메뉴 그룹 조회

- [x] 메뉴 그룹의 전체 목록을 조회한다.

### 주문
- 주문 타입
  - DELIVERY: 배달
  - TAKEOUT: 포장
  - EAT_IN: 매장 식사
- 주문 상태
  - WAITING: 대기
  - ACCEPTED: 접수
  - SERVED: 전달 완료
  - DELIVERING: 배달
  - DELIVERED: 배달 완료
  - COMPLETION: 완료

#### 주문 요청

- [x] 주문의 타입은 필수다.
- [x] 주문의 주문 품목은 필수다.
- [x] 메뉴의 갯수와 주문 품목의 갯수가 동일해야 한다.
- [x] 매장식사가 아닌 경우 주문 품목의 갯수는 0보다 커야 한다.
- [x] 노출 설정된 메뉴만 주문이 가능하다.
- [x] 메뉴의 가격과 주문 품목의 가격이 같아야 한다.
- [x] 주문 요청 성공 시 최초 상태는 대기(WAITING) 이다.
- 배달(DELIVERY)
  - [x] 배달 타입의 주문인 경우 주소는 필수값이다.
- 매장 식사(EAT_IN)
  - [x] 매장 식사 타입의 주문인 경우 주문 테이블은 필수값이다.
  - [x] 매장 식사 타입의 주문인 경우 주문 테이블은 비점유 상태여야 한다.

#### 주문 승인

- [x] 주문의 상태가 대기(WAITING)인 경우만 승인이 가능하다.
- [x] 주문의 상태를 접수(ACCEPTED)로 변경한다.
- 배달(DELIVERY)
  - [x] 주문 번호, 가격(상품*수량), 배달 주소를 라이더에게 전달한다.

#### 주문 전달

- [x] 주문의 상태가 접수(ACCEPTED)인 경우만 전달이 가능하다.
- [x] 주문의 상태를 전달 완료(SERVED)로 변경한다.

#### 주문 배달

- [x] 주문 타입이 배달(DELIVERY)인 경우만 배달이 가능하다.
- [x] 주문의 상태가 전달 완료(SERVED)인 경우만 배달이 가능하다.
- [x] 주문의 상태를 배달 중(DELIVERING)으로 변경한다.

#### 주문 배달 완료

- [x] 주문 타입이 배달 중(DELIVERING)인 경우만 배달 완료가 가능하다.
- [x] 주문의 상태를 배달 완료(DELIVERED)로 변경한다.

#### 주문 완료

- [x] 주문의 상태를 완료(COMPLETION)로 변경한다.
- 배달(DELIVERY)
  - [x] 주문 타입이 배달(DELIVERY)인 경우 주문의 상태가 배달 완료(DELIVERED)인 경우만 완료(COMPLETED)상태로 변경 가능하다.
- 포장(TAKEOUT)
  - [x] 주문 타입이 포장(TAKEOUT) 또는 매장 식사(EAT_IN)인 경우 주문의 상태가 전달 완료(SERVED)인 경우만 완료(COMPLETED)상태로 변경 가능하다.
- 매장 식사(EAT_IN)
  - [x] 매장 식사인 경우 완료되지 않은 상태인 주문이 존재하지 않는 경우 확인 테이블의 인원 수를 0, 미사용으로 변경한다.

### 주문 테이블

#### 주문 테이블 등록

- [x] 주문 테이블의 이름은 필수값이다.
- [X] 주문 테이블을 미사용 상태, 인원 수를 0으로 초기 세팅한다.

#### 주문 테이블 착석

- [x] 착석할 경우 주문 테이블의 상태가 사용으로 변경된다.

#### 주문 테이블 비점유

- [x] 주문 테이블의 상태가 미사용으로 변경된다.
- [x] 주문 테이블의 인원 수가 0으로 변경된다.
- [x] 주문 테이블의 주문들 중 완료(COMPLETED)상태가 아닌 주문이 있는 경우 클리어가 불가능하다.

#### 주문 테이블 인원 수 변경

- [x] 변경할 인원 수는 0이상이어야 한다.
- [x] 주문 테이블이 사용중일 때만 인원 수 변경이 가능하다.

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
