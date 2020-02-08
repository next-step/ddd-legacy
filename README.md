# 키친포스

## 요구 사항
- 주문
    - 주문은 주문번호, 테이블번호, 주문시각, 주문 처리 상태가 있어야 한다.
    - 주문한 음식의 정보와 수량이 있어야 한다.
    - 주문 목록 조회를 할 수 있다.
    - 주문이 추가될 수 있다.
    - 주문의 상태를 변경할 수 있다.
    - 주문의 상태는 조리중, 서빙완료, 조리완료 3가지 상태를 가진다.
- 메뉴
    - 메뉴는 메뉴명, 가격, 카테고리를 가진다.
    - 메뉴는 어느 카테고리에라도 속해야 한다.
    - 메뉴 카테고리는 카테고리명을 가진다.
    - 메뉴는 재고 정보를 가진다.
    - 메뉴 목록 조회를 할 수 있다.
    - 메뉴를 추가할 수 있다.
- 테이블
    - 테이블은 점유 되었는지 아닌지의 상태를 갖고 있어야 한다.
    - 테이블은 점유중인 사람 수에 대한 정보를 가져야 한다.
    - 테이블 목록 조회를 할 수 있다.
    - 테이블의 상태를 변경할 수 있다.
    - 테이블의 점유중인 손님 수를 변경할 수 있다.
- 상품
    - 상품은 상품명과 가격을 가진다.
    - 상품 목록을 조회할 수 있다.
    - 상품을 추가할 수 있다.
    
## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|메뉴 |menu  | 메뉴에 관한 정보가 들어있다 |
|메뉴 카테고리 |menuGroup  | 메뉴의 속성 정보 ex) 신메뉴, 한마리메뉴 |
|메뉴 재고 |menuProduct  | 메뉴의 재고 현황을 나타낸다 |
|주문 |order  | 주문에 대한 상태와 정보가 들어있다 |
|주문 항목 |orderLineItem  | 영수증에 찍히는 한줄의 정보 ex) 1번 테이블 수원왕갈비 2 |
|테이블 |orderTable  | 테이블에 대한 상태와 정보가 들어있다 |
|테이블 그룹 |tableGroup  | 테이블에 대한 상태와 정보가 들어있다 |
|상품 |product  | 상품에 대한 정보가 들어있다 |

## 모델링
