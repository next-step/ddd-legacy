# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항

> #### kitchenpos 주문 관리 시스템 제공

#### 주문타입별 주문상태 흐름도

``` mermaid
flowchart LR
대기중 --> 접수완료 
접수완료 --> 처리완료
처리완료 --> A{주문타입}
A -->|배달| 배달중
배달중 --> 배달완료
배달완료 --> 주문완료
A --> |테이크아웃 or 매장식사| 주문완료 
```

#### 주문타입(OrderType)

- 배달(DELIVERY)
- 테이크아웃(TAKEOUT)
- 매장식사(EAT_IN)

#### 주문상태(OrderStatus)

- 대기중(WAITING)
- 접수완료(ACCEPTED)
- 처리완료(SERVED)
- 배달중(DELIVERING)
- 배달완료(DELIVERED)
- 주문완료(COMPLETED)

#### 주문(Order)

- 주문을 등록한다.
    - [ ] 주문은 주문타입을 가진다.
        - [ ] 주문타입은 배달, 테이크아웃, 매장식사 3 종류가 있다.
    - [ ] 주문은 주문상태를 가진다.
        - [ ] 주문상태중 주문대기(WAITING)상태를 가진다.
    - [ ] 요청한 주문은 주문내역목록을 가진다.
        - [ ] 주문내역목록의 각각의 주문내역은 메뉴, 수량, 가격 정보를 가진다.
        - [ ] 주문내역목록의 각각의 메뉴정보는 존재해야한다.
        - [ ] 주문내역의 메뉴와 등록된 메뉴의 정보를 비교하여 주문가능한지 확인한다.
            - [ ] 요청한 메뉴는 판매가능으로 등록된 메뉴어야한다.
            - [ ] 요청한 메뉴의 가격과 등록된 메뉴의 가격은 일치해야한다.
            - [ ] 주문타입이 테이크아웃이거나 배달이면 수량은 0개 이상이여야한다.
    - [ ] 요청한 주문은 주문타입에따라 주문 수령 정보를 입력한다.
        - [ ] 주문타입이 배달인 경우 배달주소를 입력해야한다.
        - [ ] 주문타입이 매장식사인 경우 착석한 고객이 없는 테이블번호를 입력해야한다.
- 주문을 접수하다.
    - [ ] 주문상태가 대기중 상태일 경우 주문 접수가 가능하다.
    - [ ] 주문타입이 배달일 경우 라이더에게 배달요청을 한다.
    - [ ] 주문상태를 접수완료 상태로 변경한다.
- 주문을 처리하다.
    - [ ] 주문상태가 접수완료 상태일 경우 처리완료가 가능하다.
    - [ ] 주문상태를 처리완료 상태로 변경한다.
- 주문을 완료하다.
    - [ ] 주문타입에따라 주문을 완료할 수있는 상태를 확인한다.
        - [ ] 주문타입이 배달일 경우 배달완료된 주문만 주문완료 처리할 수 있다.
        - [ ] 주문타입이 테이크아웃, 매장식사일 경우 처리완료된 주문만 주문완료 처리할 수 있다.
        - [ ] 주문테이블 정리한다.
            - [ ] 주문테이블 정리는 주문상태가 주문완료상태일 경우 가능하다.
            - [ ] 주문테이블 정리시 고객 수는 0명, 착석여부는 미착석상태로 변경한다.
    - [ ] 주문상태를 주문완료 상태로 변경한다.
    - [ ] 주문타입이 매장식사일 경우 주문테이블 정리한다.
        - [ ] 주문테이블 정리시 고객 수는 0명, 착석여부는 미착석상태로 변경한다.
- 배달을 시작하다.
    - [ ] 주문타입이 배달인 경우만 배달을 시작할 수 있다.
    - [ ] 주문상태가 처리완료 상태인 경우 배달시작이 가능하다.
    - [ ] 주문상태를 배달시작 상태로 변경한다.
- 배달을 완료하다.
    - [ ] 주문타입이 배달인 경우 배달을 완료할 수 있다.
    - [ ] 주문상태가 배달중 상태인 경우 배달완료가 가능하다.
    - [ ] 주문상태를 배달완료 상태로 변경한다.

#### 상품(Product)

- 상품은 이름과 가격 정보를 가진다.
- 상품을 등록한다.
    - [ ] 상품의 가격은 0원 이상이어야 한다.
    - [ ] 상품의 이름은 0자 이상이며 비속어를 포함하면 안된다.
- 상품가격을 변경한다.
    - [ ] 상품의 가격은 0원 이상이어야 한다.
    - [ ] 상품과 관련된 메뉴의 가격을 확인한다.
        - [ ] 메뉴의 가격은 메뉴에 등록된 상품들의 총 가격(상품가격 * 상품수량) 보다 클 수 없다.
        - [ ] 메뉴의 가격보다 상품들의 총 가격이 크다면 메뉴를 판매불가 상태로 변경시킨다.

#### 메뉴(Menu)

- 메뉴는 이름, 가격, 판매여부, 상품, 메뉴그룹 정보를 가진다.
- 메뉴를 등록한다.
    - [ ] 메뉴에 등록할 메뉴그룹 정보가 존재해야한다.
    - [ ] 메뉴에 등록할 상품의 정보가 존재해야한다.
    - [ ] 메뉴에 등록할 상품의 수량은 0개 이상이여야 한다.
    - [ ] 메뉴의 가격이 메뉴에 등록된 상품들의 총 가격(상품가격 * 상품수량)보다 클 수 없다.
    - [ ] 메뉴의 이름은 0자 이상이며 비속어를 포함하면 안된다.
- 메뉴가격을 변경한다.
    - [ ] 메뉴의 가격은 0원 이상이여야 한다.
    - [ ] 메뉴의 가격은 메뉴에 등록된 상품들의 총 합계(상품가격 * 상품수량)보다 클 수 없다.
- 메뉴를 표시한다.
    - [ ] 메뉴의 가격은 메뉴에 등록된 상품들의 총 합계(상품가격 * 상품수량)보다 클 수 없다.
    - [ ] 메뉴를 판매가능 상태로 변경시킨다.
- 메뉴를 숨긴다.
    - [ ] 메뉴를 판매불가 상태로 변경시킨다.

#### 메뉴그룹(MenuGroup)

- 메뉴그룹은 이름 정보를 갖는다.
- 메뉴그룹을 등록한다.
    - [ ] 메뉴그룹의 이름은 1자 이상 이어야한다.

#### 주문테이블(OrderTable)

- 주문테이블은 이름, 고객 수, 착석여부 정보를 가진다.
- 주문테이블을 등록한다.
    - [ ] 주문테이블의 이름은 1자 이상이여야한다.
    - [ ] 주문테이블의 고객의 수는 0명이다.
    - [ ] 주문테이블의 착석여부는 미착석 상태다.
- 주문테이블에 착석한다.
    - [ ] 주문테이블의 착석여부를 착석됨상태로 변경한다.
- 주문테이블 정리한다.
    - [ ] 주문테이블 정리는 주문상태가 주문완료상태일 경우 가능하다.
    - [ ] 주문테이블 정리시 고객 수는 0명, 착석여부는 미착석상태로 변경한다.
- 주문테이블의 고객 수를 변경한다.
    - [ ] 주문테이블의 고객의 수는 0명 이상이여야 한다.
    - [ ] 주문테이블에 고객이 착석했다면 고객 수는 변경이 불가능하다.

## 용어 사전

| 한글명 | 영문명 | 설명 |
|-----|-----|----|
|     |     |    |

## 모델링
