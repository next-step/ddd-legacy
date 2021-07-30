# 키친포스

## 요구 사항

Kitchen Pos 구현

도메인 종류

- 메뉴 그룹
- 메뉴
- 제품
- 주문테이블

요구 상세

메뉴그룹

- [ ] 메뉴그룹을 만들 수 있다.
- [ ] 메뉴 그룹은 이름과 메뉴 그룹을 특정하기 위한 고유한 값을 기준으로 만들어 진다.
- [ ] 같은 이름의 메뉴 그룹을 생성 할 수 있다.
- [ ] 모든 메뉴 그룹을 조회할 수 있다.

제품

- [ ] 제품을 등록 할 수 있다.
- [ ] 제품은 제품을 특정하기 위한 특정값과 가격, 이름으로 이루어 진다.
- [ ] 제품은 같은 이름의 제품이 존재 할 수 있다.
- [ ] 제품의 가격은 0원 보다 커야 한다.
- [ ] 제품 이름은 비속어로 이루어 질 수 없다.
- [ ] 제품의 가격은 변경 될 수 있다.
- [ ] 한 제품은 여러개의 메뉴에 속할 수 있다.
- [ ] 모든 제품을 조회 할 수 있다.

메뉴

- [ ] 메뉴를 등록 할 수 있다.
- [ ] 메뉴는 메뉴를 특정하기 위한 고유한 값과 메뉴 이름, 가격, 메뉴에 속한 제품들로 이루어 진다.
- [ ] 메뉴에는 가격과 제품이 반드시 책정 되어야 한다.
- [ ] 메뉴가격은 구성된 제품 가격의 합보다 클 수 없다.
- [ ] 메뉴 이름은 비속어로 이루어 질 수 없다.
- [ ] 메뉴는 동일한 이름의 메뉴가 존재 할 수 있다.
- [ ] 메뉴는 하나의 특정 메뉴그룹에 속한다.
- [ ] 메뉴는 한개 혹은 여러 종류의 제품으로 구성된다.
- [ ] 메뉴에 구성된 한 제품의 수량은 한개 이상이다.
- [ ] 메뉴 가격은 변경 될 수 있다
- [ ] 메뉴의 가격은 0보다 작을 수 없다.
- [ ] 메뉴는 display 속성에 의해 사용자에게 보여지거나 숨겨진다.
- [ ] 메뉴의 display 속성은 변경 될 수 있다.
- [ ] 메뉴의 가격이 0보다 작을 경우 해당 메뉴는 화면에 노출 될 수 없다.
- [ ] 모든 메뉴를 조회 할 수 있다.

주문 테이블

- [ ] 주문 테이블은 실제 키친의 테이블 좌석이다.
- [ ] 주문 테이블을 생성 할 수 있다.
- [ ] 주문 테이블은 각각의 이름, 이용자 수, 자리 사용 유무로 이루어진다.
- [ ] 주문 테이블에 손님이 착석하게 되면 다른 사람은 사용 할 수 없다.
- [ ] 주문 테이블은 손님이 나가게 되면 해당 주문 테이블은 다시 이용가능한 상태가 된다.
- [ ] 주문 테이블은 이용자 수를 변경 할 수 있다.
- [ ] 이용자 수 변경시 해당 수는 0보다 작을 수고 비어있는 테이블은 수정 불가 하다.
- [ ] 모든 테이블을 조회할 수 있다.


## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |

## 모델링
