# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항

- 키친포스를 구현한다.
- 메뉴그룹
    - [ ] 메뉴그룹은 이름을 가지고 있다.
    - [ ] 메뉴그룹을 등록할 수 있다. 등록될 때 아이디는 고유한 식별자로 저장이 된다.
    - [ ] 등록 된 모든 메뉴그룹을 조회할 수 있다.
- 메뉴상품
    - [ ] 메뉴상품은 상품과, 수량을 가지고 있다.
- 상품
    - [ ] 상품은 이름과 가격을 가지고 있다.
    - [ ] 상품을 가격은 0보다 작을 수 없다.
    - [ ] 상품의 이름은 비속어가 들어가거나, 비어있을 수 없다.
    - [ ] 가격을 변경하는 경우, 상품이 포함된 메뉴들의 가격이 메뉴의 가격보다 이하인 경우 해당 메뉴를 전시 하지 않는다.
    - [ ] 등록된 상품을 모두 조회할 수 있다.
- 메뉴
    - [ ] 메뉴는 아이디, 이름, 가격, 메뉴그룹, 전시여부, 메뉴상품들을 가지고 있다.
    - [ ] 메뉴의 등록할 때
        - [ ] 메뉴의 가격은 0보다 크고, 상품의 가격 * 수량 보다 작거나 같아야 한다.
        - [ ] 메뉴그룹의 경우 미리 등록 되어있어야 한다.
        - [ ] 메뉴상품의 경우 비어있을 수 없다.
        - [ ] 메뉴상품의 경우 상품이 등록 되어 있어야 한다.
        - [ ] 메뉴상품의 경우 수량은 0보다 커야 한다.
        - [ ] 메뉴의 이름은 비어있거나, 욕설이 포함될 수 없다. (API 이용)
    - [ ] 메뉴를 전시할 때 메뉴의 가격은 0보다 크고, 상품의 가격 * 수량 보다 작거나 같아야 한다.
    - [ ] 메뉴를 전시하지 않을 수 있다.
    - [ ] 등록된 메뉴를 모두 조회할 수 있다.
- 주문 테이블
    - [ ] 이름, 손님 수, 사용할 수 있는 테이블 여부를 가진다.
    - [ ] 테이블의 이름은 비어있을 수 없다.
    - [ ] 테이블에 손님이 앉으면, 테이블은 다른손님이 사용할 수 없다.
    - [ ] 주문의 상태가 종료되지 않으면 테이블을 치울 수 없다.
    - [ ] 테이블을 치우는 경우 손님의 수와, 테이블 여부가 초기화 된다.
    - [ ] 테이블의 손님수를 변경하는 경우
        - [ ] 손님의 수는 0보다 작을 수 없다.
        - [ ] 테이블이 사용할 수 없는 경우 변경할 수 없다.
    - [ ] 등록된 주문 테이블을 모두 조회할 수 있다.
- 주문 라인 아이템
    - [ ] 메뉴, 수량, 가격을 가진다.
- 주문
    - [ ] 주문은 주문타입, 주문상태, 준문날짜, 주문라인아이템들, 배송지, 주문 테이블을 가진다.
        - [ ] 주문타입은 배달 주문, 포장 주문, 매장주문을 가진다.
        - [ ] 주문상태는 대기, 접수, 주문제공, 배달중, 배달완료, 완료를 가진다.
    - [ ] 주문 라인 아이템은 비어있을 수 없다.
    - [ ] 주문 된 메뉴는 이미 등록 되어 있어야 한다.
    - [ ] 주문타입이 EAT_IN이 아닌 경우, 수량은 0보다 작을 수 없다.
    - [ ] 주문 된 메뉴가 전시되지 않으면 주문할 수 없다.
    - [ ] 메뉴의 가격과, 주문된 가격이 다를 수 없다.
    - [ ] 주문타입이 배달주문인 경우 배송지를 입력해야 한다.
    - [ ] 주문타입이 매장주문 인 경우 사용할 수 있는 테이블이 있어야 한다.
    - [ ] 주문 수락
        - [ ] 주문 수락의 경우 주문상태가 대기만 가능하다.
        - [ ] 주문 상태가 배달주문 인 경우 배달을 요청한다.
        - [ ] 주문이 수락되는 경우, 주문상태를 접수로 변경한다.
    - [ ] 주문 제공
        - [ ] 주문이 제공되는 경우 주문상태가 접수만 가능하다.
        - [ ] 주문이 제공되면 주문상태를 SERVED 로 변경한다.
    - [ ] 배달 시작
        - [ ] 배달이 시작되는 경우 주문타입이 배달주문만 가능하다.
        - [ ] 배달이 시작되는 경우 주문상태가 주문제공만 가능하다.
        - [ ] 배달이 시작되는 경우 주문상태를 DELIVERING 으로 변경한다.
    - [ ] 배달 종료
        - [ ] 배달이 종료되는 경우 주문상태가 배달주문만 가능하다.
        - [ ] 배달이 종료되는 경우 주문상태를 배달완료로 변경한다.
    - [ ] 주문 종료
        - [ ] 주문이 종료되는 경우 주문타입은 배달주문 인데, 주문상태가 배달완료면 안된다.
        - [ ] 주문타입이 포장주문 또는, 매장주문인 경우, 주문상태는 주문제공이면 안된다.
        - [ ] 주문을 종료하는 경우 주문상태를 완료 로 변경한다.
        - [ ] 주문타입이 매장주문 경우, 해당 테이블의 종료된 상태면 손님의 수와, 테이블 여부를 초기화 한다.
    - [ ] 등록된 주문상품 모두 조회할 수 있다.
- 메뉴 상품
    - [ ] 상품을 등록한다.

## 용어 사전

| 한글명   | 영문명           | 설명                          |
|-------|---------------|-----------------------------|
| 메뉴그룹  | menuGroup     | 메뉴그룹                        |
| 메뉴상품  | menuProduct   | 메뉴를 구성하는 상품                 |
| 상품    | product       | 메뉴에 등록된 상품                  |
| 메뉴    | menu          | 메뉴                          |
| 주문테이블 | orderTable    | 주문 테이블                      |
| 주문    | order         | 주문                          |
| 주문메뉴  | orderLineItem | 주문된 메뉴                      |
|       |               |                             |
| 주문 타입 | orderType     | eatIn, 배달주문, takeOut 을 가진다. |
| 매장주문  | eatIn         | 매장에서 먹는 주문                  |
| 배달주문  | delivery      | 배달 주문                       |
| 포장주문  | takeOut       | 포장 주문                       |
|       |               |                             |
| 주문 상태 | orderStatus   | 주문 상태                       |
| 대기    | waiting       | 주문이 대기중인 상태 접수 전            |
| 접수    | accepted      | 주문이 접수된 상태                  |
| 주문제공  | served        | 주문이 제공된 상태                  |
| 배달중   | delivering    | 주문이 배달중인 상태                 |
| 배달완료  | delivered     | 주문이 배달완료인 상태                |
| 완료    | completed     | 주문이 모두 완료된 상태               |

## 모델링
