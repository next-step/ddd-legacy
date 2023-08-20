# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항

- 키친포스(POS)를 구현한다.
- 키친포스는 음식점에 메뉴를 주문하는 시스템이다.
- 키친포스는 메뉴, 메뉴 그룹, 상품, 테이블, 주문으로 구성된다.
- 메뉴와 메뉴 그룹 상품 관계는 다음과 같다

____

- 식사 ==> 메뉴 그룹
    - 후라이드 ==> 메뉴
        - 후라이드 치킨 ==> 상품
        - 콜라 ==> 상품
    - 양념 ==> 메뉴
        - 양념 치킨 ==> 상품
        - 사이다 ==> 상품

____

### 메뉴

- [] 메뉴는 1개 이상의 상품으로 구성된다.
- [] 메뉴를 등록할 수 있다.
- [] 메뉴 가격은 상품의 총 합이다.
- [] 메뉴 가격을 변경할 수 있다.
- [] 메뉴를 노출할 수 있다.
- [] 메뉴를 숨길 수 있다.
- [] 메뉴는 '메뉴 그룹'에 포함되어야 한다.
- [] 메뉴 전체를 조회할 수 있다.

### 메뉴 그룹

- [] 메뉴 그룹을 등록할 수 있다.
- [] 메뉴 그룹을 조회할 수 있다.

### 상품

- [] 상품을 등록할 수 있다.
- [] 상품 가격을 변경할 수 있다.
- [] 상품 전체를 조회할 수 있다.

### 테이블

- [] 테이블은 매장 내 테이블을 의미한다.
- [] 테이블을 등록할 수 있다.
- [] 테이블에 손님이 있으면, 점유 여부를 점유로 변경한다.
- [] 테이블에 손님이 떠나면, 점유 여부를 미점유로 변경하고, 테이블의 손님 수를 0으로 초기화한다.
- [] 테이블의 상태가 완료가 아니면, 점유 상태를 미점유로 변경할 수 없다.
- [] 테이블의 손님수를 변경할 수 있다.
- [] 테이블 전체를 조회할수 있다.

### 주문

- [] 주문은 주문, 조리완료, 서빙완료, 배달, 배달완료, 완료 상태를 갖는다.
- [] 주문은 매장내 식사, 테이크 아웃, 배달로 구분 된다.
    - 주문
        - [] 주문은 1개 이상의 메뉴로 구성된다.
        - [] 주문을 생성할 수 있다.
        - [] 주문 타입은 필수 값이다.
        - [] 메뉴에 없는 상품을 주문할 수 없다.
        - [] 노출되지 않은 메뉴는 주문할 수 없다.
        - [] 메뉴 가격과 주문 상품의 가격은 같아야 한다.
        - [] 주문을 하면 상태를 '대기중' 상태로 변경한다.
        - [] 배달 주문인 경우 주소가 반드시 필요하다.
        - [] 매장 주문인 경우 테이블이 반드시 필요하다.
    - 조리완료
        - [] 조리가 완료되면 '조리완료' 상태로 변경한다.
        - [] 주문 상태가 '대기중'일 때만 '조리완료'로 변경할 수 있다.
        - [] 배달 주문은 '배달' 요청을 한다.
    - 서빙완료
        - [] 서빙이 완료되면 '서빙완료' 상태로 변경한다.
    - 배달
        - [] 배달이 시작되면 '배달중' 상태로 변경한다.
        - [] 주문 타입이 배달 타입이 아니면 '배달중' 상태로 변경할 수 없다.
        - [] 주문 상태가 '조리완료'일 때만 '배달중'으로 변경할 수 있다.
    - 배달완료
        - [] 배달이 완료되면 '배달완료' 상태로 변경한다.
        - [] 주문 상태가 '배달중'일 때만 '배달완료' 상태로 변경 가능하다.
    - 완료
        - [] 배달 주문은 배달이 완료되면 '완료' 상태로 변경한다.
        - [] 매장 주문은 서빙이 완료되면 '완료' 상태로 변경한다.
        - [] 매장 내 주문인 경우 테이블을 비운다.

## 용어 사전

| 한글명 | 영문명 | 설명 |
|-----|-----|----|
|     |     |    |

## 모델링
