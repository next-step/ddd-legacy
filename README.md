# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항

### MenuGroup
- [ ]  메뉴 그룹은 이름을 갖는다.
- 메뉴 그룹 생성
  - [ ]  이름은 1자 이상이어야 한다.
- 메뉴 그룹 리스트 조회
  - [ ]  모든 메뉴 그룹을 리스트로 조회할 수 있다.

### Menu
- [ ]  메뉴는 이름, 가격, 메뉴그룹ID, display 여부, 메뉴상품 리스트를 갖는다.
- 메뉴 생성
  - 메뉴 이름
    - [ ]  메뉴의 이름은 1자 이상이어야 한다.
    - [ ]  메뉴의 이름은 욕설을 포함할 수 없다.
  - 메뉴 가격
    - [ ]  메뉴의 가격은 0원 이상이어야 한다.
    - [ ]  메뉴의 가격은 메뉴상품 리스트에 포함된 각 메뉴상품 가격(메뉴상품 가격 * 수량)의 합보다 작아야 한다.
  - 메뉴 그룹ID
    - [ ]  메뉴가 속할 메뉴 그룹의 ID이다.
  - 메뉴 상품 리스트
    - [ ]  메뉴 상품을 하나 이상 가져야 한다.
    - [ ]  메뉴 상품은 상품ID와 수량을 갖는다.
    - [ ]  메뉴 상품의 수량은 0 이상이어야 한다.
- 메뉴 가격 수정
  - [ ]  메뉴 ID를 통해 가격을 수정할 수 있다.
  - [ ]  메뉴의 가격은 0 이상이고, 메뉴상품 리스트에 포함된 각 메뉴상품 가격(메뉴상품 가격 * 수량)의 합보다 작아야 한다.
- 메뉴 표시하기
  - [ ]  메뉴 ID를 통해 메뉴를 표시할 수 있다.
  - [ ]  메뉴의 가격은 0 이상이고, 메뉴상품 리스트에 포함된 각 메뉴상품 가격(메뉴상품 가격 * 수량)의 합보다 작아야 한다.
- 메뉴 숨기기
  - [ ]  메뉴 ID를 통해 메뉴를 숨길 수 있다.
- 메뉴 리스트 조회
  - [ ]  모든 메뉴를 리스트로 조회할 수 있다.

### Order
- 주문 타입
  - [ ]  주문 타입에는 배달, 테이크아웃, 매장식사가 있다.
- 주문 생성
  - [ ]  주문 타입을 입력받고 주문 타입에 따라 주문 테이블ID, 주문 아이템 리스트, 배달지 주소를 입력받는다.
  - [ ]  주문 상태에는 대기, 승인, 제공완료, 배달중, 배달완료, 완료가 있다.
  - 주문 아이템 리스트
    - [ ]  주문 아이템을 하나 이상 포함해야 한다.
    - [ ]  주문 아이템은 메뉴 ID, 가격, 수량을 갖는다.
    - [ ]  숨겨져 있는 메뉴는 주문 아이템에 담을 수 없다.
    - [ ]  주문 아이템의 가격은 메뉴의 가격과 같아야 한다.
    - [ ]  주문 타입이 매장식사가 아닐 때, 주문 아이템의 수량은 0 이상이어야 한다.
  - [ ]  주문 상태를 대기로 초기화한다.
  - [ ]  주문 타입이 배달인 경우, 배달지 주소를 반드시 입력해야 한다.
  - [ ]  주문 타입이 매장식사인 경우, 주문 테이블이 비어있으면 안된다.
- 주문 승인
  - [ ]  주문 상태가 대기여야 한다.
  - [ ]  주문 타입이 배달인 경우, 라이더에게 배달을 요청한다.
  - [ ]  주문 상태를 승인으로 변경한다.
- 주문 제공완료
  - [ ]  주문 상태가 승인이어야 한다.
  - [ ]  주문 상태를 제공완료로 변경한다.
- 주문 배달 시작
  - [ ]  주문 타입이 배달이어야 한다.
  - [ ]  주문 상태가 제공완료여야 한다.
  - [ ]  주문 상태를 배달중으로 변경한다.
- 주문 배달 완료
  - [ ]  주문 상태가 배달중이어야 한다.
  - [ ]  주문 상태를 배달완료로 변경한다.
- 주문 완료
  - [ ]  주문 타입이 배달인 경우, 주문 상태가 배달완료여야 한다.
  - [ ]  주문 타입이 테이크아웃이거나 매장식사인 경우, 주문 상태가 제공완료여야 한다.
  - [ ]  주문 상태를 완료로 변경한다.
  - [ ]  주문 타입이 매장식사인 경우, 주문 테이블의 손님 수를 0으로 초기화하고 비어 있는 상태로 변경한다.
- 주문 리스트 조회
  - [ ]  모든 주문을 리스트로 조회할 수 있다.

### OrderTable
- [ ]  주문 테이블은 이름을 갖는다.
- 주문 테이블 생성
  - [ ]  주문 테이블의 이름은 1자 이상이어야 한다.
  - [ ]  주문 테이블의 손님 수를 0으로 초기화한다.
  - [ ]  주문 테이블을 비어있는 상태로 초기화한다.
- 주문 테이블 착석
  - [ ]  주문 테이블 ID를 통해 해당 테이블을 사용중으로 변경한다.
- 주문 테이블 초기화
  - [ ]  해당 주문 테이블의 주문 상태가 완료인 경우에만 초기화할 수 있다.
  - [ ]  주문 테이블의 손님 수를 0으로 초기화한다.
  - [ ]  주문 테이블을 비어있는 상태로 초기화한다.
- 주문 테이블 손님 수 변경
  - [ ]  주문테이블 ID를 통해 손님 수를 변경할 수 있다.
  - [ ]  손님 수는 0이상이어야 한다.
- 주문 테이블 리스트 조회
  - [ ]  모든 주문 테이블을 리스트로 조회할 수 있다.

### Product
- [ ]  상품은 이름과 가격을 갖는다.
- 상품 생성
  - [ ]  상품의 이름은 욕설을 포함할 수 없다.
  - [ ]  상품의 가격은 0원 이상이어야 한다.
- 상품 가격 수정
  - [ ]  상품 ID를 통해 상품 가격을 수정할 수 있다.
  - [ ]  상품의 가격은 0원 이상이어야 한다.
  - [ ]  해당 상품을 포함하는 모든 메뉴에 대하여 메뉴 가격이 각 메뉴상품의 가격 (상품가격 * 수량)의 합보다 큰 경우, 해당 메뉴를 숨긴다.
- 상품 리스트 조회
  - [ ]  모든 상품을 리스트로 조회할 수 있다.


## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
