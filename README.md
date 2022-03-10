# 키친포스

## 요구 사항

---
### 상품(Product)
- [x] 상품을 등록 할 수 있다
    - [x] 상품은 반드시 상품명을 가지며, 비속어가 포함될 수 없다
    - [x] 상품은 반드시 상품가격을 가지며, 0원 이상이어야 한다
- [x] 상품의 가격을 변경할 수 있다
    - [x] 변경하려는 상품의 가격은 0원 이상이어야 한다    
    - [x] 가격변경시 상품을 포함하고있는 메뉴 가격이 각 상품 가격의 합보다 클경우 메뉴의 판매를 중단한다
- [x] 전체 상품리스트를 조회할 수 있다

### 메뉴(Menu)
- [x] 메뉴를 등록 할 수 있다
  - [x] 반드시 0원 이상의 메뉴가격을 가져야 한다
  - [x] 메뉴는 반드시 하나의 메뉴그룹을 지정하여 등록해야 한다    
  - [x] 메뉴는 반드시 하나 이상의 메뉴구성상품(menuProduct)을 포함하고 있어야 한다
  - [x] 상품(product)의 개수와 메뉴구성상품(menuProduct)의 개수는 일치해야 한다
  - [x] 각 메뉴구성상품(menuProduct)의 양(quantity)은 0이상이어야 한다
  - [x] 메뉴는 존재하는 상품만 포함할 수 있다
  - [x] 메뉴의 가격이 각 메뉴구성상품의 합보다 클 수 없다
  - [x] 메뉴는 반드시 메뉴명을 가지고 있어야 하며, 비속어를 포함할 수 없다
- [x] 메뉴의 가격을 변경할 수 있다
  - [x] 변경하려는 메뉴의 가격은 존재해야하며, 0원 이상이어야 한다
  - [x] 존재하는 메뉴만 가격을 변경할 수 있다  
  - [x] 메뉴 가격을 각 메뉴구성상품 가격의 합보다 크게 변경할 수 없다
- [x] 메뉴의 판매상태를 판매중(display)으로 변경할 수 있다
  - [x] 존재하는 메뉴만 판매상태를 판매중으로 변경할 수 있다
  - [x] 메뉴 가격이 각 메뉴구성상품 가격의 합보다 큰경우 판매중으로 변경할 수 없다
- [x] 메뉴의 판매상태를 판매중단(hide)으로 변경할 수 있다
  - [x] 존재하는 메뉴만 판매상태를 판매중단으로 변경할 수 있다
- [x] 전체 메뉴를 조회할 수 있다

### 메뉴그룹 (MenuGroup)
- [x] 메뉴그룹을 등록할 수 있다
    - [x] 반드시 한글자 이상다의 메뉴그룹명을 가진다
- [x] 전체 메뉴그룹을 조회할 수 있다


### 주문 (Orders)
- [x] 주문을 등록할 수 있다
    - [x] 주문을 등록할때 반드시 주문형태를 선택해야 한다
    - [x] 주문은 반드시 하나 이상의 주문구성메뉴를 포함하고 있어야 한다
    - [x] 주문의 갯수와 주문구성메뉴의 갯수가 다를 수 없다
    - [x] 매장식사가 아닌 경우, 주문구성메뉴의 갯수(quantity)는 0 이상이어야 한다
    - [x] 존재하는 메뉴만 주문할 수 있다
    - [x] 판매중인 메뉴만 주문할 수 있다
    - [x] 주문형태가 배달인 경우 반드시 주문주소를 포함하고 있어야 한다
    - [x] 주문형태가 매장식사인 경우 반드시 주문 테이블을 포함하고 있어야 한다
    - [x] 공석이 아닌 주문테이블에 주문을 등록할 수 없다
- [ ] 주문상태를 주문수락으로 변경할 수 있다
    - [ ] 주문수락으로 변경 후 주문형태가 배달인 경우 배달 라이더를 요청한다    
    - [ ] 주문상태가 수락대기인 경우만 주문수락으로 변경 한다
- [ ] 주문상태를 서빙완료로 변경할 수 있다
    - [ ] 주문상태가 주문수락인 경우만 서빙완료로 변경 한다
- [ ] 주문상태를 배달중으로 변경할 수 있다
    - [ ] 주문상태가 서빙완료인 경우만 배달중으로 변경한다
    - [ ] 주문형태가 배달인 경우만 배달중으로 변경한다
- [ ] 주문상태를 배달완료로 변경할 수 있다
    - [ ] 변경 후 주문형태가 매장식사인 경우 주문 테이블의 착석여부를 공석으로 변경하고 착석인원을 0으로 바꾼다    
    - [ ] 주문형태가 배달인 경우, 주문상태가 배달중인 경우만 주문종결로 변경한다
    - [ ] 주문형태가 매장식사 또는 테이크아웃인경우, 주문상태가 서빙완료인경우만 주문종결로 변경한다
- [ ] 전체 주문을 조회할 수 있다


### 주문테이블 (OrderTable)
- [x] 주문테이블을 등록할 수 있다
    - [x] 주문테이블은 반드시 한글자 이상의 이름을 가진다
- [x] 테이블의 착석여부를 착석으로 변경할 수 있다
    - [x] 존재하는 테이블만 착석으로 변경할 수 있다
- [x] 테이블의 착석여부를 공석으로 변경할 수 있다
    - [x] 변경 후 착석인원을 0으로 변경한다    
    - [x] 존재하는 테이블만 공석으로 변경할 수 있다 
    - [x] 주문의 상태가 주문종결인 경우만 공석으로 변경할 수 있다
- [x] 주문테이블의 착석인원을 변경한다
    - [x] 착석인원은 최소 0명 이상이어야 한다
    - [x] 존재하는 테이블만 착석인원을 변경할 수 있다
    - [x] 테이블이 공석일때는 착석인원을 변경할 수 없다
- [x] 전체 주문 테이블을 조회할 수 있다
---
## 용어 사전
(한글 ㄱ-ㅎ 순)

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
| 가격 | price | 가격 |
| 공석 | clear | 테이블에 손님이 없는 상태 |
| 메뉴 | Menu | 메뉴 |
| 메뉴그룹 | MenuGroup | 메뉴그룹 |
| 메뉴상품구성| OrderProduct | 메뉴를 구성하고 있는 상품의 구성 |
| 매장식사 | EAT_IN | 주문형태-매장식사 |
| 배달 | DELIVERY | 주문형태-배달 |
| 배달중 | DELIVERED | 음식 배달이 완료된 상태 |
| 상품 | product | 상품 |
| 서빙완료 | SERVED | 음식이 서빙이 완료된 상태 <br/> 테이크아웃, 매장식사 -> 고객에게 제공 <br/> 배달 -> 배달원 도착을 기다림|
| 주문수락 | WAITING  | 주문이 생성되어 매장의 확인을 기다리는 상태 |
| 주문 | orders | 주문 |
| 주문메뉴구성| OrderLineItem | 주문을 구성하고 있는 메뉴의 구성 |
| 주문상태 | OrderStatus | 음식의 준비상태 |
| 주문수락 | ACCEPTED | 주문을 확인하고 매장에서 수락한 상태 |
| 주문종결| COMPLETED |주문이 종결된 상태 <br/> 매장식사 -> 주문 종결 후 주문 테이블 정리 가능|
| 주문테이블 | order_table | 주문테이블 |
| 주문형태 | OrderType | 음식의 주문 상태 |
| 착석 | sit | 테이블에 손님이 있는 상태 |
| 착석여부 | empty | 테이블에 손님이 있으면 착석, 손님이 없으면 공석 |
| 착석인원 | number-of-guests | 주문테이블에 착석한 사람 수 |
| 테이크아웃 | TAKEOUT | 주문형태-테이크아웃 |
| 판매중 | display | 메뉴를 판매하는 상태 |
| 판매중단 |hide | 메뉴를 판매하지 않는 상태 |
| 판매상태 | displayed | 메뉴의 판매상태여부 |


## 모델링
