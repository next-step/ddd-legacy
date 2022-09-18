# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항

### 상품

* 새로운 상품을 등록할 수 있다.
* 상품의 이름을 설정할 수 있다.
  * 이름은 반드시 설정해야 한다.
  * 이름은 비속어를 포함할 수 없다.
* 상품의 가격을 설정할 수 있다.
  * 가격은 반드시 설정해야 한다.
  * 가격은 음수일 수 없다.
  * 가격을 변경할 수 있다. 가격을 변경하는 경우에도 위 조건을 만족해야 한다.
    * 상품의 가격이 변경된 경우 메뉴의 가격이 메뉴에 포함된 모든 상품 가격의 합보다 크면 메뉴가 노출되지 않도록 변경된다.
* 상품은 메뉴에 포함될 수 있다.
  * 한 상품이 여러 메뉴에 포함될 수 있다.
* 모든 상품 목록을 조회할 수 있다.

### 메뉴

* 새로운 메뉴를 등록할 수 있다.
* 메뉴의 이름을 설정할 수 있다.
  * 이름은 반드시 설정해야 한다.
  * 이름은 비속어를 포함할 수 없다.
* 메뉴의 가격을 설정할 수 있다.
  * 가격은 반드시 설정해야 한다.
  * 가격은 음수일 수 없다.
  * 가격은 메뉴에 포함된 상품 가격의 합보다 작거나 같아야 한다.
  * 가격을 변경할 수 있다. 가격을 변경하는 경우에도 위 조건을 만족해야 한다.
* 메뉴는 하나의 메뉴 그룹에 포함되어야 한다.
* 메뉴의 노출 여부를 설정할 수 있다.
  * 노출 여부는 변경할 수 있다.
  * 메뉴의 가격이 메뉴에 포함된 상품 가격의 합보다 큰 상품은 노출시킬 수 없다.
* 메뉴에 포함된 상품과 각 상품별 수량을 설정할 수 있다.
  * 메뉴에 포함된 상품이 없을 수는 없다.
  * 한 메뉴에 여러 종류의 상품이 포함될 수 있다.
  * 메뉴에 포함된 각 상품별 수량은 음수일 수 없다.
* 모든 메뉴 목록을 조회할 수 있다.

### 메뉴 그룹

* 새로운 메뉴 그룹을 등록할 수 있다.
* 메뉴 그룹의 이름을 설정할 수 있다.
  * 이름은 반드시 설정해야 한다.
  * 이름은 비어있을(`''`) 수 없다.
* 메뉴 그룹에는 하나 이상의 상품이 포함될 수 있다.
* 모든 메뉴 그룹 목록을 조회할 수 있다.

### 주문 테이블

* 새로운 주문 테이블을 등록할 수 있다.
  * 주문 테이블이 생성될 때 손님 수는 `0`명이다.
  * 주문 테이블이 생성될 때 점유되어 있지 않은 상태다.
* 주문 테이블의 이름을 설정할 수 있다.
  * 이름은 반드시 설정해야 한다.
  * 이름은 비어있을(`''`) 수 없다.
* 주문 테이블에 있는 손님 수를 설정할 수 있다.
  * 손님 수는 음수일 수 없다.
  * 손님 수를 변경할 수 있다. 손님 수를 변경하는 경우에도 위 조건을 만족해야 한다.
    * 주문 테이블이 점유되어 있지 않은 상태에서는 손님 수를 변경할 수 있다.
* 주문 테이블이 점유중인지 여부를 설정할 수 있다.
  * 주문 테이블을 점유되어 있는 상태로 변경할 수 있다.
  * 주문 테이블을 점유되어 있지 않은 상태로 변경할 수 있다.
    * 주문 테이블에서 요청한 주문이 아직 완료되지 않은 경우 주문 테이블을 점유되어 있지 않은 상태로 변경할 수 없다. 
    * 주문 테이블이 점유되어 있지 않은 상태로 변경되면 그 주문 테이블의 손님 수도 `0`명으로 변경된다.
* 모든 주문 테이블 목록을 조회할 수 있다.

### 주문

* 새로운 주문을 등록할 수 있다.
  * 주문이 생성될 때 주문 상태는 대기중이다.
  * 주문 시각은 주문이 생성될 때 생성 시점으로 설정된다.
* 주문의 유형을 설정할 수 있다.
  * 주문 유형은 매장, 포장, 배달 중 하나여야 한다.
* 주문의 상태를 설정할 수 있다.
  * 주문 상태는 대기중, 접수, 서빙 완료, 배달중, 배달 완료, 완료 중 하나여야 한다.
  * 주문을 접수 상태로 변경할 수 있다.
    * 주문이 대기중 상태가 아닌 경우 접수 상태로 변경할 수 없다.
    * 배달 주문인 경우 주문이 접수되면 배달 요청을 접수한다.
  * 주문을 서빙 완료 상태로 변경할 수 있다.
    * 주문이 접수되지 않은 경우 서빙 완료 상태로 변경할 수 없다.
  * 주문을 배달중 상태로 변경할 수 있다.
    * 배달 주문이 아닌 경우 배달중 상태로 변경할 수 없다.
    * 주문이 서빙된 경우에만 주문을 배달중 상태로 변경할 수 있다.
  * 주문을 배달 완료 상태로 변경할 수 있다.
    * 주문이 배달중인 경우에만 주문을 배달 완료 상태로 변경할 수 있다.
  * 주문을 완료 상태로 변경할 수 있다.
    * 배달 주문인 경우 배달이 완료되어야 주문을 완료 상태로 변경할 수 있다.
    * 포장 주문이거나 매장 주문인 경우 서빙되어야 주문을 완료 상태로 변경할 수 있다.
    * 매장 주문인 경우 주문이 완료 상태로 변경하면 그 주문을 요청한 주문 테이블을 점유되지 않은 상태로 변경하고 그 주문 테이블의 손님 수도 `0`명으로 변경한다.
* 주문에 포함된 메뉴를 설정할 수 있다.
  * 주문은 하나 이상의 메뉴를 포함해야 한다.
  * 매장 주문이 아닌 경우 각 메뉴의 수량은 음수일 수 없다.
  * 노출되지 않도록 설정된 메뉴는 주문에 포함될 수 없다.
* 배달 주소를 설정할 수 있다.
  * 배달 주문인 경우 배달 주소를 반드시 설정해야 한다.
  * 배달 주문인 경우 배달 주소는 비어있을(`''`) 수 없다.
* 주문 테이블을 설정할 수 있다.
  * 매장 주문인 경우 주문 테이블이 반드시 존재해야 한다.
  * 주문 테이블을 점유되어 있지 않은 주문 테이블로 설정할 수 없다.
* 모든 주문 목록을 조회할 수 있다.

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
