# 키친포스

## 요구 사항

- 간단한 POS를 구현한다.
- 메뉴 그룹
  - [ ] 메뉴 그룹을 생성할 수 있다.
    - [ ] 메뉴 이름이 존재해야한다.
  - [ ] 메뉴 그룹 목록을 조회할 수 있다.
- 메뉴
  - [ ] 메뉴를 생성할 수 있다.
    - [ ] 메뉴의 그룹정보가 존재해야한다.
    - [ ] 메뉴의 가격은 0보다 작을 수 없다.
    - [ ] 메뉴의 가격은 메뉴 상품들의 합보다 클 수 없다.
    - [ ] 메뉴 상품의 정보가 존재해야한다.
    - [ ] 메뉴 상품 갯수 해당하는 상품과 갯수가 일치해야한다.
    - [ ] 메뉴 상품의 수량은 0보다 작을 수 없다.
  - [ ] 메뉴의 가격을 변경할 수 있다.
    - [ ] 변경하려는 가격 정보가 존재해야한다.
    - [ ] 변경하려는 가격은 0보다 작을 수 없다.
    - [ ] 메뉴의 가격은 메뉴 상품들의 합보다 클 수 없다.
  - [ ] 메뉴 목록을 조회할 수 있다.
  - [ ] 메뉴 이름이 존재해야한다.
  - [ ] 메뉴 이름에 비속어가 들어가면 안된다.
  - [ ] 메뉴의 가격 정보가 있어야한다.
  - [ ] 메뉴 전시여부를 활성화 할 수 있다.
    - [ ] 메뉴 정보가 존재해야한다.
    - [ ] 메뉴의 가격은 메뉴 상품들의 합보다 클 수 없다.
  - [ ] 메뉴 전시여부를 비활성화 할 수 있다.
    - [ ] 메뉴 정보가 존재해야한다.
  - [ ] 메뉴 상품 목록정보가 비어있으면 안된다.
- 메뉴상품
  - [ ] 메뉴 상품은 수량이 존재해야한다.
  - [ ] 메뉴 상품은 상품 정보가 존재해야한다.
  - [ ] 메뉴 상품은 메뉴 정보가 존재해야한다.
- 주문
  - [ ] 주문을 생성할 수 있다.
    - [ ] 메뉴를 입력받아야한다.
    - [ ] 주문 라인 아이템이 비어있으면 안된다.
    - [ ] 식당내 식사가 아닐때는 주문 라인 아이템의 수량은 0보다 커야한다.
    - [ ] 메뉴의 전시 상태가 활성화 되어야한다.
    - [ ] 메뉴가격은 주문 라인 아이템과 가격이 같아야한다.
    - [ ] 최초 주문시에 대기 상태이다.
    - [ ] 주문 형태가 배달이면 배달 주소가 존재해야한다.
    - [ ] 주문 형태가 식당내 식사일 경우 테이블 정보가 존재해야한다.
    - [ ] 주문 형태가 식당내 식사일 경우 테이블이 비어있으면 주문할 수 없다.
  - [ ] 주문 상태에는 대기, 승인, 조리, 배달중, 배달중, 배달완료, 주문완료가 있다.
  - [ ] 주문 상태를 변경할 수 있다.
    - [ ] 승인 상태로 변경시 대기에서 변경해야한다.
    - [ ] 승인 상태로 변경시 주문 번호가 존재해야한다.
    - [ ] 승인 상태로 변경시 주문 타입이 배달이면 주문 라인 아이템의 합을 계산한다.
    - [ ] 승인 상태로 변경시 주문 타입이 배달이면 키친 라이더 클라이언트에 배달 요청을 한다.
    - [ ] 조리 상태로 변경시 주문 번호가 존재해야한다.
    - [ ] 조리 상태로 변경시 승인에서 변경해야한다.
    - [ ] 배달중으로 변경시 주문 번호가 존재해야한다.
    - [ ] 배달중으로 변경시 주문 형태가 배달이야 한다.
    - [ ] 배달중으로 변경시 조리에서 변경해야한다.
    - [ ] 배달완료로 변경시 주문 번호가 존재해야한다.
    - [ ] 배달완료로 변경시 배달중에서 변경해야한다.
    - [ ] 주문완료로 변경시 주문 번호가 존재해야한다.
    - [ ] 주문완료로 변경시 주문형태가 배달이면 주문상태 배달완료에서 변경해야한다.
    - [ ] 주문완료로 변경시 주문형태가 태이크 아웃이면 주문상태 조리에서 변경해야한다.
    - [ ] 주문완료로 변경시 주문형태가 식당내 식사면 주문상태 조리에서 변경해야한다.
    - [ ] 주문완료로 변경시 주문형태가 식당내 식사이고 테이블 정보가 있으면 주문 테이블의 0으로 변경하고 빈 상태로 둔다.
  - [ ] 주문 시간이 존재한다.
  - [ ] 배달 주소가 존재할 수 있다.
  - [ ] 포장, 주문, 식당식사를 선택할 수 있다.
  - [ ] 주문 목록을 조회할 수 있다.
- 주문 라인 아이템
  - [ ] 수량이 존재한다.
  - [ ] 메뉴정보가 존재한다.
  - [ ] 주문 정보가 존재한다.
- 키친 라이더 클라이언트
  - [ ] 주문 번호, 가격, 배달 주소를 전달 받아 배달 요청한다.
- 상품
  - [ ] 상품을 등록할 수 있다.
    - [ ] 상품은 가격이 존재해야한다.
    - [ ] 상품은 가격이 0 보다 커야한다
    - [ ] 상품의 이름이 존재해야 한다.
    - [ ] 상품이름에 비속어가 들어가면 안된다.
  - [ ] 상품을 조회할 수 있다.
  - [ ] 상품의 가격을 변경 할 수 있다.
    - [ ] 상품은 가격이 존재해야한다.
    - [ ] 상품은 가격이 0 보다 커야한다
    - [ ] 상품의 정보가 존재해야한다.
    - [ ] 메뉴가격이 메뉴 상품의 합보다 크다면 메뉴 전시상태를 비활성화한다.
- 테이블
  - [ ] 테이블을 등록할 수 있다.
    - [ ] 테이블 이름이 존재해야 한다.
    - [ ] 최초의 테이블 손님수는 0이다.
    - [ ] 최초의 테이블은 비어있는 상태가 활성화이다.
  - [ ] 테이블 목록을 조회할 수 있다.
  - [ ] 테이블의 앉은 상태 설정할 수 있다.
    - [ ] 테이블 정보가 존재해야한다.
    - [ ] 비어있는 상태가 비활성화 된다.
  - [ ] 테이블을 해제할 수 있다.
    - [ ] 테이블 정보가 존재해야한다.
    - [ ] 해제된 테이블의 손님수은 0이다.
    - [ ] 비어있는 상태가 활성화 된다.
  - [ ] 테이블의 손님수를 변경할 수 있다.
    - [ ] 변경하려는 손님의 수는 0보다 작을 수 없다.
    - [ ] 테이블 정보가 존재해야한다.
    - [ ] 테이블이 비어있는 상태에서는 변경할 수 없다.

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
| 포스 | POS | 판매시점 정보관리를 담당하는 기기 |
| 메뉴 | MENU | 음식의 목록과 가격을 담은 목록 |
| 메뉴상품 | MENU PRODUCT | 메뉴에 담기는 상품과 갯수 |
| 가격 | PRICE | 메뉴를 구입하기 위해 제공하는 재화 |
| 전시 | DISPLAY | 메뉴를 손님이 확인할 수 있는 여부 |
| 수량 | QUANTITY | 상품의 갯수 |
| 주문 | ORDER | 손님이 식사를 하기위해 메뉴를 지정하는 행위 |
| 주문 상태 | ORDER STATUS | 주문이 실행되면 실행되는 단계 |
| 주문 라인 아이템 | ORDER LINE ITEM | 주문에 담기는 세부 주문 정보 |
| 대기 | WAITING | 주문이 실행되면 가게의 승인을 기다리는 단계 |
| 승인 | ACCEPTED | 가게의 주문 승인이 된 단계 |
| 조리 | SERVED | 음식을 만드는 단계 |
| 배달중 | DELIVERING | 조리된 음식을 손님에게 배달중인 상태 |
| 배달완료 | DELIVERED | 조리된 음식을 손님에게 배달완료한 상태 |
| 주문완료 | COMPLETED | 주문이 종료된 상태 |
| 주문형태 | ORDER TYPE | 주문을 전달받는 형태 |
| 배달 | DELIVERY | 손님이 식사를 주문하여 음식 전달받는 형태 |
| 포장 | TAKEOUT | 손님이 식사를 주문하여 음식을 직접 가져가는 형태 |
| 식당식사 | EAT IN | 손님이 식사를 주문하여 식당에서 식사를 하는 형태 |
| 배달주소 | DELIVERY ADDRESS | 손님의 주문형태가 배달일 때 주문을 전달할 장소 |
| 키친 라이더 클라이언트 | KITCHEN RIDERS CLIENT | 배달요청을 하기 위해 사용되는 클라이언트 |
| 앉음 | SIT | 손님이 테이블을 사용하는 상태 |
| 해제 | CLEAR | 손님이 테이블을 사용하지 않는 상태 |
| 테이블 | TABLE | 손님이 식당에서 식사를 하면 식사를 하게되는 곳 |

## 모델링
