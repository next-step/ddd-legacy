# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항

### 1. 테이블

- [x] 테이블은 매장 내 테이블을 의미한다. 손님이 앉아 메뉴를 주문할 수 있다.
- [x] 전체 테이블 현황을 확인할 수 있다.
- [x] 새로운 테이블을 생성할 수 있다.
    - [x] 테이블의 이름은 비워둘 수 없다.
- [x] 손님은 테이블에 앉을 수 있다.
    - [x] 손님이 1명이라도 앉게 되면 해당 테이블은 채워진 상태가 된다.
- [x] 테이블을 비울 수 있다.
    - [x] 단, 완료되지 않은 주문이 있을 경우 해당 테이블을 비울 수 없다.
- [x] 테이블에 앉은 손님의 수를 수정할 수 있다.
    - [x] 단, 수정할 손님의 수는 0 미만일 수 없다.
    - [x] 손님의 수를 수정하기 위해서는 테이블은 채워진 상태여야 한다.

### 2. 메뉴 그룹

- [x] 메뉴 그룹은 메뉴를 모아 범주화한 목록이다. e.g) 신메뉴, 한마리메뉴
- [x] 전체 메뉴 그룹을 확인할 수 있다.
- [x] 새로운 메뉴 그룹을 생성할 수 있다.
    - [x] 메뉴그룹의 이름은 비워둘 수 없다.

### 3. 메뉴

- [x] 메뉴는 상품목록 및 각 상품 수량을 연결해두어 주문할 수 있는 단위다.
- [x] 메뉴는 전시상태를 가진다. 전시 상태는 손님에게 해당 메뉴를 전시할지를 결정하는 상태다.
- [x] 메뉴의 가격은 연결한 상품 가격 미만이어야 한다.
    - e.g. 개당 5000원 상품 2개: 메뉴가격 10000원 미만 필요
- [x] 전체 메뉴 목록을 확인할 수 있다.
- [x] 새로운 메뉴를 생성할 수 있다.
    - [x] 메뉴가격의 이름은 비워둘 수 없다.
    - [x] 메뉴그룹의 이름은 비워둘 수 없다.
    - [x] 상품의 이름은 비워둘 수 없다.
    - [x] 메뉴는 메뉴그룹이 있어야 생성할 수 있다.
    - [x] 메뉴 등록 시점에 존재하지 않는 상품은 메뉴에 포함할 수 없다.
- [x] 특정 메뉴의 가격을 수정할 수 있다.
    - [x] 가격은 비워둘 수 없다.
- [x] 특정 메뉴를 전시상태로 변경할 수 있다.
    - [x] 메뉴의 가격은 연결한 상품 가격 미만이어야 한다. 이상이라면 전시상태로 변경할 수 없다.
- [x] 특정 메뉴를 미전시상태로 변경할 수 있다.

### 4. 주문

- [x] 전체 주문 목록을 확인할 수 있다.
- [x] 주문유형에는 `배달`, `포장`, `매장식사`가 있다.
- [x] 주문상태는 `대기`, `수락`, `조리완료`, `배달 중`, `배달완료`, `완료` 상태가 있다.
- [x] 새로운 주문을 생성할 수 있다.
    - [x] 주문유형은 비워둘 수 없다.
    - [x] 주문항목은 비워둘 수 없다.
    - [x] 주문유형이 `배달`일 경우 배달지 주소는 비워둘 수 없다.
    - [x] 주문유형이 `매장식사`일 경우 테이블이 존재해야 한다.
    - [x] 주문유형이 `매장식사`일 경우 해당 테이블이 채워져있으면 주문할 수 없다.
    - [x] 주문유형이 `매장식사`가 아닐 경우 주문수량은 0 미만일 수 없다.
    - [x] 주문 시점의 가격이 해당 메뉴 가격과 다를 경우 주문할 수 없다.
    - [x] 새로운 주문 생성 시 최초 주문상태는 `대기` 상태가 된다.
- [x] 특정 주문의 주문상태를 `수락` 상태로 변경할 수 있다.
    - [x] 주문상태가 `대기` 상태일 때만 `수락` 상태로 변경할 수 있다.
    - [x] 주문유형이 `배달`일 경우 배달기사를 요청한다.
- [x] 특정 주문의 주문상태를 `조리완료` 상태로 변경할 수 있다.
    - [x] 주문상태가 `수락` 상태일 때만 `조리완료` 상태로 변경할 수 있다.
- [x] 특정 주문의 주문상태를 `배달 중` 상태로 변경할 수 있다.
    - [x] 주문유형이 `배달` 유형일 때만 변경할 수 있다.
    - [x] 주문상태가 `조리완료` 상태일 때만 변경할 수 있다.
- [x] 특정 주문의 주문상태를 `배달완료` 상태로 변경할 수 있다.
    - [x] 주문상태가 `배달 중`일 때만 변경할 수 있다.
- [x] 특정 주문의 주문상태를 `완료` 상태로 변경할 수 있다.
    - [x] 주문유형이 `배달`일 경우 배달상태가 `배달완료`일 때만 변경할 수 있다.
    - [x] 주문유형이 `포장`, `매장식사`일 경우 배달상태가 `조리완료`일 때만 변경할 수 있다.
    - [x] 주문유형이 `매장식사`일 경우 테이블 비움 상태를 확인하고 테이블을 비운다.

### 5. 상품

- [x] 전체 상품 목록을 확인할 수 있다.
- [x] 새로운 상품을 생성할 수 있다.
    - [x] 상품명은 비워둘 수 없다.
    - [x] 상품가격은 비워둘 수 없다.
- [x] 특정 상품의 가격을 수정할 수 있다.
    - [x] 상품의 가격 수정 시 해당 상품이 연결된 메뉴 가격이 변경되는 가격보다 높을 경우 해당 메뉴를 미전시한다.

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
