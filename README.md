# 키친포스

## 요구 사항
- 간단한 주문 앱을 제작한다

### 메뉴 그룹 (Menu Group)
- [ ] 메뉴 그룹을 등록한다
    - [ ] 메뉴 그룹 이름은 한글자 이상이다
    - [ ] 메뉴 그룹의 식별자를 생성한다
- [ ] 등록된 메뉴 그룹을 모두 조회한다

### 메뉴 (Menu)
- [ ] 메뉴를 등록한다
    - [ ] 메뉴 그룹의 식별자를 생성한다
    - [ ] 메뉴 가격은 0 이상이다
    - [ ] 메뉴는 등록되어 있는 메뉴 그룹에 포함된다
    - [ ] 메뉴 상품은 모두 상품으로 등록되어 있어야 한다.
    - [ ] 메뉴 상품의 수량은 0 보다 작을 수 없다.
    - [ ] 메뉴의 가격은 메뉴 상품들의 (가격 * 수량) 합을 넘을 수 없다.
    - [ ] 메뉴 이름은 한글자 이상이어야 한다.
    - [ ] 메뉴 이름은 욕설을 포함하지 않는다.
- [ ] 메뉴의 가격을 수정한다
    - [ ] 메뉴 가격은 1보다 커야한다
    - [ ] 메뉴 가격은 메뉴 상품들의 (가격 * 수량) 합을 넘을 수 없다.
- [ ] 메뉴를 노출한다
    - [ ] 등록된 메뉴만 노출한다
    - [ ] 노출주려는 메뉴의 가격은 메뉴에 등록된 메뉴 상품의 가격의 합보다 커야한다
- [ ] 메뉴를 숨긴다
    - [ ] 등록된 메뉴만 숨길 수 있다
- [ ] 모든 메뉴를 조회한다

### 주문 (Order)
- [ ] 주문한다
    - [ ] 주문은 배달, 포장, 매장식사 셋 중에 하나다
    - [ ] 주문 상품은 하나 이상이다
    - [ ] 주문한 상품이 등록된 상품에 있어야 한다
    - [ ] 주문 상품 수량은 매장 식사가 아닌 경우 하나 이상 주문해야 한다
    - [ ] 메뉴는 모든 노출되어 있어야 한다
    - [ ] 메뉴의 가격과 각 주문 항목의 가격은 일치해야 한다
    - [ ] 배달 주문인 경우, 배달 주소가 빈 값일 수 없다
    - [ ] 매장 식사의 경우, 주문 테이블이 비어있지 않아야 한다
- [ ] 주문을 수락한다
    - [ ] 대기 중인 주문 상태만 수락가능하다
    - [ ] 배달 주문인 경우, 주문 정보를 라이더에게 전달한다
    - [ ] 주문 상태를 수락으로 변경한다
- [ ] 주문을 서빙한다
    - [ ] 수락된 주문 상태만 서빙할 수 있다
    - [ ] 주문 상태를 서빙 완료로 변경한다
- [ ] 배달을 시작한다
    - [ ] 배달 주문만 배달 가능하다
    - [ ] 서빙 완료 상태의 주문만 배달 가능하다
    - [ ] 주문 상태를 배달 중으로 변경한다
- [ ] 배달을 완료한다
    - [ ] 배달 주문만 배달을 완료할 수 있다
    - [ ] 주문 상태를 배달 완료로 변경한다
- [ ] 주문이 완료된다
    - [ ] 배달 주문인 경우, 주문 상태가 배달 완료이어야 하다
    - [ ] 포장이나 매장 식사의 경우, 주문 상태가 서빙 완료이어야 한다
    - [ ] 주문 상태를 완료 상태로 변경한다
    - [ ] 매장 식사인 경우, 주문 테이블의 고객 수를 0명 그리고 빈 자리로 변경한다
- [ ] 모든 주문을 조회한다

### 주문 테이블 (Order Table)
- [ ] 주문 테이블을 등록한다
    - [ ] 테이블 이름은 반드시 가진다
    - [ ] 테이블의 식별자를 생성한다
    - [ ] 테이블 등록 시, 테이블의 고객은 0명이고 비어있는다.
- [ ] 고객이 테이블에 앉는다
    - [ ] 고객이 테이블에 앉으면 테이블을 채운다
- [ ] 테이블을 치운다
    - [ ] 주문이 완료된 테이블을 치울 수 없다
    - [ ] 테이블을 치우면 고객은 0명이고 비어 있도록 한다
- [ ] 테이블의 고객 숫자가 변경된다 
    - [ ] 고객의 숫자가 0명 이상 이어야 한다
    - [ ] 비어있는 테이블은 고객 숫자를 변경할 수 없다
- [ ] 모든 가게 테이블을 조회한다

### 상품 (Product)
- [ ] 상품을 등록한다
    - [ ] 상품의 가격은 0 이상 이어야 한다
    - [ ] 상품 이름에 욕설을 허용하지 않는다  
- [ ] 상품 가격을 변경한다
    - [ ] 변경될 상품 가격은 0이상 이어야 한다
    - [ ] 등록된 상품만 가격을 변경할 수 있다
    - [ ] 상품 가격 변경 시, 상품이 속한 메뉴의 가격이 상품들의 가격의 합보다 크면 숨김 처리한다
- [ ] 모든 상퓸울 조회한다


## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
