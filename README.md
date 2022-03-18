# 키친포스

## 요구 사항


---
## API

- 간단한 식당 포스기기를 구현한다.
  
- 메뉴그룹
  - [X] 메뉴들이 속해 있는 메뉴 그룹이 존재한다.
  - [X] 사용자는 메뉴그룹을 생성할수 있다.
    - [X] 메뉴그룹 생성시 공백이 아닌 이름을 지정해야한다.
  - [X] 메뉴그룹의 목록을 조회할 수 있다.
  
- 메뉴
  - [X] 사용자는 메뉴를 등록할 수 있다.
    - [X] 메뉴의 가격은 0원 이상이여야한다.
    - [X] 메뉴 등록시 1개 이상의 상품이 포함되어야 한다.
    - [X] 메뉴에 포함되는 각 상품은 0개 이상 포함되어야한다.
    - [X] 등록하려는 메뉴의 가격은 메뉴에 포함되는 상품의 총 가격보다 비싸면안된다.
    - [X] 메뉴의 이름은 필수이고 비속어는 사용할 수 없다.
  - [X] 사용자는 등록된 메뉴의 가격을 수정 할 수 있다.
    - [X] 메뉴의 가격은 0원 이상이여야 한다.
    - [X] 수정하려는 가격은 메뉴에 포함된 상품의 총 가격보다 비싸면안된다.
  - [X] 사용자는 메뉴를 노출 상태로 변경할 수 있다.
    - [X] 메뉴에 포함된 상품의 총 가격보다 메뉴의 가격이 비쌀경우 노출상태로 변경할 수 없다. 
  - [X] 사용자는 메뉴를 비노출 상태로 변경할 수 있다.
  - [X] 메뉴의 목록을 조회할 수 있다.
  
- 주문테이블
  - [X] 사용자는 주문 테이블을 등록할 수 있다.
    - [X] 주문테이블의 이름은 필수로 지정해야한다.
    - [X] 주문테이블 생성시 손님수는 0명에 빈테이블로 설정된다.
  - [X] 주문테이블의 상태를 착성으로 변경 할 수 있다.
  - [X] 주문테이블의 상태를 빈테이블로 변경 할 수 있다.
    - [X] 해당 테이블에 완료되지 않은 주문이 존재하면 빈테이블로 변경 할 수 없다.
  - [X] 테이블의 착석 사람수를 변경할 수 있다.
    - [X] 사람수는 0명 이상이여야한다.
    - [X] 빈테이블은 사람수를 변경할 수 없다.
  - [X] 테이블의 목록을 조회 할수 있다.

- 주문
  - [X] 주문의 타입은 매장식사/포장/배달이 있다.
  - [X] 고객은 주문을 생성할수 있다.
    - [X] 주문의 타입은 필수이다.
    - [X] 주문 하려는 상품은 1개 이상 존재해야한다.
    - [X] 매장식사가 아닐 경우 주문하려는 상품은 0개 이상주문해야한다.
    - [X] 비노출된 메뉴는 주문할 수 없다.
    - [X] 주문생성을 하면 처음상태는 대기 상태로 된다.
    - [X] 배달주문일 경우 배달지 주소는 필수이다.
    - [X] 매장식사일 경우 테이블에 착성후 가능하다.
  - [X] 주문의 상태는 대기/접수/서빙/배달시작/배달완료/주문완료 의 상태가 존재한다.
  - [X] 주문을 접수한다.
    - [X] 주문의 상태를 접수로 변경하는것은 대기 상태 일 경우만 가능하다.
  - [X] 주문을 서빙한다.
    - [X] 서빙상태로 변경하는것은 접수된 주문만 가능하다.
  - [X] 주문을 배달한다.
    - [X] 주문 타입이 배달인것만 배달상태로 변경가능하다.
    - [X] 배달상태로 변경하는것은 서빙상태의 주문만 가능하다.
  - [X] 주문을 배달완료로 변경한다.
    - [X] 배달중인 주문만 배달완료로 변경이 가능하다.
  - [X] 주문의 상태로 완료 변경한다.
    - [X] 주문타입이 배달일 경우 배달이 완료된 주문만 완료처리할수 있다.
    - [X] 포장이나 매장식사의 경우 서빙상태만 완료처리 할수 있다.
    - [X] 모든 주문이 완료된 테이블은 주문완료시 빈테이블로 변경한다.
  - [X] 주문의 목록을 볼수 있다.
  
- 상품
  - [X] 사용자는 상품을 등록할 수 있다.
    - [X] 상품의 가격은 0원 이상이여야 한다.
    - [X] 상품의 이름은 필수이고 비속어가 될수 없다.
  - [X] 사용자는 등록된 상품의 가격을 변경 할 수 있다.
    - [X] 상품의 가격은 0원 이상이여야 한다.
    - [X] 상품이 등록된 메뉴의 가격이 메뉴에 포함된 상품들의 총 가격보다 비싸면 메뉴는 비노출 처리한다.
  - [X] 사용자는 등록된 상품의 목록을 조회 할 수 있다.
  

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
