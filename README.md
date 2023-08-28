# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항
- 메뉴
  - 메뉴 생성
    - [x] 메뉴는 생성할 수 있다.
    - [x] 메뉴는 이름, 가격, 그룹id, 노출여부 정보로 구성되어 있다.
    - [x] 메뉴는 메뉴그룹에 속해야만 한다.
    - [x] 메뉴는 하나 이상의 메뉴상품을 포함하고 있어야 한다.
      - [x] 메뉴상품의 수량은 0개 이상이어야 한다.
      - [x] 상품이 존재해야 메뉴상품을 등록할 수 있다.
    - [x] 메뉴의 가격은 0원 이상이어야 한다.
    - [x] 메뉴의 가격은 포함된 상품들 가격의 합(상품가격 * 수량) 보다 작아야 한다.
    - [x] 메뉴 이름은 아무 값이 없거나 욕 또는 비속어가 포함되면 안된다.
  - 메뉴 가격 변경
    - [ ] 메뉴의 가격을 변경할 때 메뉴가 등록되어 있어야 한다.
    - [ ] 메뉴의 가격이 메뉴상품들 가격의 합(상품가격 * 수량) 보다 작거나 같으면 가격을 변경할 수 있다.
  - 메뉴 노출
    - [ ] 메뉴의 가격이 메뉴상품들 가격의 합(상품가격 * 수량) 보다 작거나 같으면 노출할 수 있다.
  - 메뉴 감추기
    - [ ] 메뉴는 화면에 노출 안할 수 있다.
  - 메뉴 목록을 조회

- 메뉴그룹
  - 메뉴 그룹 생성
    - [ ] 메뉴그룹은 생성할 수 있다.
    - [ ] 메뉴그룹의 이름은 반드시 존재해야 한다.
  - 메뉴그룹 목록 조회

- 상품
  - 상품 생성
    - [ ] 상품은 생성할 수 있다.
    - [ ] 상품은 이름, 가격 정보로 구성되어 있다.
    - [ ] 상품의 가격은 0원 이상이어야 한다.
    - [ ] 상품 이름은 아무 값이 없거나 욕 또는 비속어가 포함되면 안된다.
  - 상품 가격 변경
    - [ ] 기존 상품의 가격을 변경할 수 있다. 
    - [ ] 상품을 포함하고 있는 메뉴의 메뉴상품들 가격의 합(상품가격 * 수량) 보다 크면 화면에 노출하지 않는다.
  - 상품 목록 조회

- 주문
  - 주문 생성
    - [ ] 주문은 생성할 수 있다.
    - [ ] 주문은 배달주소, 주문유형, 주문상태, 주문일, 테이블id 정보로 구성되어 있다.
    - [ ] 주문 유형은 배달, 포장, 매장식사 3가지 유형이 존재한다.(각 유형별 설명은 용어집 참고)
    - [ ] 주문 상태는 대기중, 수락됨, 제고됨, 배달중, 배달됨, 완료됨 6가지 상태가 존재한다.(각 상태별 설명은 용어집 참고)
    - [ ] 주문 아이템은 하나 이상 존재해야 한다.
    - [ ] 주문 유형이 배달,포장이면 수량이 0보다 커야 한다.
    - [ ] 화면에 노출되지 않은 메뉴는 주문할 수 없다.
    - [ ] 메뉴의 가격이 주문 아이템의 가격과 같아야 한다.
    - [ ] 처음 주문이 생성됐을 때, 주문상태가 대기중 상태다.
    - [ ] 처음 주문이 생성됐을 때, 주문일은 현재 시간이다.
    - [ ] 주문 유형이 배달유형일 때, 배달주소는 반드시 존재해야 한다.
    - [ ] 주문 유형이 매장식사일 때, 테이블이 반드시 존재해야 한다.
    - [ ] 주문 유형이 매장식사일 때, 테이블이 비어있어야 한다.
  - 주문 수락
    - [ ] 주문 상태가 대기중 상태 여야 한다.
    - [ ] 주문 유형이 배달일 때, 라이더에게 배달을 요청한다
    - [ ] 주문 상태를 수락됨 상태로 변경한다.
  - 주문 제공
    - [ ] 주문 상태가 수락됨 상태여야 한다.
    - [ ] 주문 상태를 제공됨 상태로 변경한다.
  - 배달 시작
    - [ ] 주문 유형이 배달이어야 한다.
    - [ ] 주문 상태가 제공됨 상태 이어야 한다.
    - [ ] 주문 상태를 배달중으로 변경한다.
  - 배달 완료
    - [ ] 주문 상태가 배달중 이어야 한다.
    - [ ] 주문 상태를 배달됨 으로 변경한다.
  - 주문 완료
    - [ ] 주문 유형이 배달일 때, 주문 상태가 배달됨 이어야 한다.
    - [ ] 주문 유형이 포장이나 매장식사일 때, 주문 상태가 제공됨 이어야 한다.
    - [ ] 주문 상태를 완료됨 상태로 변경한다.
  - 주문 목록을 조회할 수 있다.

- 주문 아이템
  - [ ] 주문 아이템은 주문이 생성될 때 함께 생성된다.
  - [ ] 주문 아이템은 수량, 메뉴id, 주문id 정보로 구성되어 있다.
  - [ ] 주문 아이템은 특정 메뉴에 속해있다.
  - [ ] 주문 아이템은 특정 주문에 속해있다.

- 테이블
  - 테이블 생성
    - [ ] 테이블은 생성할 수 있다.
    - [ ] 테이블은 이름, 손님수, 빈테이블 여부 정보로 구성되어 있다.
    - [ ] 테이블 이름은 반드시 존재해야 한다.
    - [ ] 테이블을 처음 생성하면 빈테이블이다.
  - 테이블 착석
    - [ ] 테이블 빈테이블 여부를 변경한다.
  - 테이블 비움
    - [ ] 테이블 주문 상태가 완료여야 비울 수 있다.
    - [ ] 테이블을 빈테이블로 변경해줘야 한다.
  - 테이블 인원 수 변경
    - [ ] 테이블 손님수는 0명보다 커야 한다.
    - [ ] 테이블 인원이 가득차있어야 인원 수를 변경할 수 있다.
  - 테이블 목록을 조회할 수 있다.

## 용어 사전

| 한글명   | 영문명           | 설명                          |
|-------|---------------|-----------------------------|
| 메뉴    | Menu          | 메뉴                          |
| 메뉴그룹  | MenuGroup     | 메뉴가 속한 그룹                   |
| 메뉴상품  | MenuProduct   | 메뉴가 포함하고 있는 상품              |
| 상품    | Product       | 메뉴에 등록할 수 있는 제품             |
| 주문    | Order         | 주문                          |
| 주문아이템 | OrderLineItem | 주문에 포함된 메뉴                  |
| 테이블   | OrderTable    | 테이블                         |
| 배달    | DELIVERY      | 고객에게 주문된 제품을 전달하는 주문유형      |
| 포장    | TAKEOUT       | 고객이 직접 방문하여 가져가는 주문유형       |
| 매장식사  | EAT_IN        | 고객이 매장에서 식사하는 주문유형          |
| 대기중   | WAITING       | 주문이 접수 되는것을 기다리는 주문상태       |
| 수락됨   | ACCEPTED      | 가게에서 주문을 받아들였다는 주문상태        |
| 제공됨   | SERVED        | 포장, 매장식사 고객에게 메뉴를 전달한 주문상태  |
| 배달중   | DELIVERING    | 고객이 주문한 제품을 배송지로 전달중인 주문상태  |
| 배달됨   | DELIVERED     | 고객이 주문한 제품을 배송지에서 받았다는 주문상태 |
| 완료됨   | COMPLETED     | 주문 프로세스가 끝났다는 주문상태          |
| 배달주소  | deliveryAddress | 고객이 주문한 제품을 받는 주소           |
| 라이더   | kitchenriders | 고객에게 주문을 전달하는 사람           |

## 모델링
