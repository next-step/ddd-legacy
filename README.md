# 키친포스

## 요구 사항

치킨집의 포스기계에서 사용하는 어플리케이션을 구현한다.

### 상품(Products)

- [ ] 상품을 생성할 수 있다.
  - [ ] 상품을 생성할 때 상품의 가격을 반드시 입력해야 한다.
  - [ ] 상품을 생성할 때 상품의 가격은 반드시 양수를 입력해야 한다.
  - [ ] 상품의 이름은 중복될 수 없다.
- [ ] 상품 목록을 볼 수 있다.

### 메뉴그룹(MenuGroup)

- [ ] 메뉴그룹을 검색할 수 있다
- [ ] 메뉴그룹을 생성할 수 있다.
  - [ ] 메뉴그룹 이름은 중복될 수 없다.
  - [ ] 메뉴그룹 이름은 비어 있을수 없다. 

### 메뉴(Menus)

- [ ] 메뉴를 생성할 수 있다.
  - [ ] 메뉴는 이름이 있으며, 비속어를 포함할 수 없다.
  - [ ] 메뉴의 가격은 0보다 커야한다.
  - [ ] 메뉴 가격이 상의 총액을 넘을 수 없다.
  - [ ] 메뉴는 메뉴 그룹에 존재 해야한다.
  - [ ] 메뉴에 담기는 상품이 존재해야한다.
    -  [ ] 품목별 수량은 1보다 큰 값을 가져야 한다.
  - [ ] 상품의 총액 = 상품의 가격 * 상품의 수량
  - [ ] 메뉴 진열 여부를 선택 할 수 있다.
- [ ] 각 메뉴는 반드시 하나 이상의 메뉴그룹에 속한다.
- [ ] 메뉴 리스트를 검색할 수 있다.
- [ ] 메뉴의 가격을 수정할 수 있다.
  - [ ] 메뉴의 가격은 반드시 0보다 큰 값을 가지며, 메뉴에 속한 모든 상품의 가격의 합보다 클 수 없다.


### 주문(Orders)

- [ ] 새로운 주문을 생성할 수 있다.
  - [ ] 주문의 종류로 배달(DELIVERY), 테이크아웃(TAKEOUT), 홀이용(EAT_IN) 중 하나를 반드시 선택해야한다.
  - [ ] 주문에는 이미 등록된 메뉴가 포함되어 있어야 한다.
  - [ ] 주문의 종류가 배달 혹은 테이크아웃인 경우 주문을 구성하는 각 메뉴의 수량은 1개 이상이어야 한다.   
  - [ ] 주문에 포함된 메뉴는 제공 가능한 메뉴여야 한다.(노출되어 있는 메뉴여야 한다)
  - [ ] 주문의 종류가 배달인 경우 반드시 주소를 입력해야한다.
  - [ ] 주문의 종류가 홀이용인 경우 이용할 테이블 식별값을 입력해야한다.
- [ ] 주문을 수락할 수 있다.
  - [ ] 주문 수락 시점에 주문의 상태가 WAITING(대기) 상태여야 한다.
  - [ ] 주문이 수락되면 주문의 상태는 ACCEPTED(수락됨) 으로 변경된다.
- [ ] 주문을 서빙할 수 있다.
  - [ ] 서빙 시점에 주문의 상태가 ACCEPTED(수락됨) 상태여야 한다.
  - [ ] 주문에 대한 서빙이 발생하면 해당 주문의 상태는 SERVED(서빙됨) 로 변경된다.
- [ ] 주문을 배달 시작 처리 할 수 있다.
  - [ ] 주문의 종류가 DELIVERY(배달)이어야 한다.
  - [ ] 배달 시작 시점에 주문의 상태가 SERVED(서빙됨) 상태이면 안된다.
  - [ ] 주문에 대한 배달이 시작되면 주문의 상태가 DELIVERING(배달중) 으로 변경된다.
- [ ] 주문을 배달 완료 처리할 수 있다.
  - [ ] 주문의 종류가 DELIVERY(배달)이어야 한다.
  - [ ] 배달 완료 시점에 주문의 상태가 DELIVERING(배달중) 상태여야 한다.
  - [ ] 주문에 대한 배달이 완료되면 주문의 상태가 DELIVERED(배달됨) 으로 변경된다.
- [ ] 주문을 완료 처리할 수 있다.
  - [ ] 주문 종류가 DELIVERY(배달) 이면서, 주문이 아직 배달중인 주문(주문의 상태가 DELIVERED(배달됨)인 주문)은 완료 처리할 수 없다.
  - [ ] 주문의 종류가 TAKEOUT(테이크아웃) 또는 EAT_IN(홀이용) 인데 아직 서빙이 되지 않은(주문의 상태가 SERVED가 아닌) 경우 해당 주문을 완료 처리할 수 없다.
  - [ ] 주문의 종류가 EAT_IN(홀이용) 인 경우 이용한 테이블의 사용 인원을 0명 처리하고 테이블의 상태를 빈 테이블로 변경한다.
  - [ ] 주문 완료 시점에 주문의 상태가 COMPLETED(완료됨) 로 변경된다.
  - [ ] 모든 주문을 조회할 수 있다.

### 테이블(Tables)

- [ ] 테이블을 생성할 수 있다.
  - [ ] 생성할 테이블의 이름을 입력해야 한다.
  - [ ] 테이블 생성시점의 해당 테이블의 이용객 수는 0 명으로 처리해야 한다.
  - [ ] 테이블 생성시점의 해당 테이블의 이용 여부는 '비어있음' 상태로 처리해야 한다.
- [ ] 테이블에 대해 착석 처리할 수 있다.
  - [ ] 테이블 착석 처리 시점에 테이블의 이용 여부가 '비어있지 않음' 상태로 처리되어야 한다.
- [ ] 테이블에 대해 정리 처리를 할 수 있다.
  - [ ] 정리처리를 하는 시점에 해당 테이블에서 발생된 주문의 상태가 COMPLETED(완료됨) 이어야 한다.
  - [ ] 테이블 정리 시점에 해당 테이블의 이용객 수는 0 명으로 변경되어야 한다.
  - [ ] 테이블 정리 시점에 해당 테이블의 이용 여부는 '비어있음' 상태로 변경 되어야 한다.
- [ ] 테이블의 인원 수를 변경할 수 있다.
- [ ] 모든 테이블을 조회할 수 있다.


## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링

## 기타

