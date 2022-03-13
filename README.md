# 키친포스

## 요구 사항

- 음식점에서 손님이 음식을 고르고 주문하기까지 일련의 절차를 구현한다.
- 메뉴 그룹
    - [x] 메뉴가 속하는 상위 분류이다.
    - [x] 메뉴 그룹을 등록할 수 있다.
    - [x] 메뉴 그룹의 이름은 빈 값이 될 수 없다.
    - [x] 모든 메뉴 그룹 조회가 가능해야 한다.

- 메뉴
    - [ ] 메뉴는 상품의 조합으로 이루어져 있다.
    - [ ] 메뉴는 생성이 가능해야 한다.
    - [ ] 메뉴는 메뉴 그룹에 소속되어 있어야 한다.
    - [ ] 메뉴는 상품이 1개 이상 포함되어야 한다. 같은 종류의 상품을 여러 개 가질 수도 있다.
    - [ ] 메뉴에 등록할 상품은 가게에 등록된 상품이어야 하며 수량은 0개 이상이어야 한다.
    - [ ] 메뉴의 이름은 빈 값이 될 수 없다.
    - [ ] 메뉴는 판매중 혹은 비매품 상태로 설정할 수 있다.
    - [ ] 메뉴의 가격은 포함된 상품의 가격을 모두 더한 값보다 커야 한다.
    - [ ] 메뉴의 가격은 변경이 가능하다.
    - [ ] 모든 메뉴 조회가 가능해야 한다.

- 상품
    - [ ] 상품은 판매가 가능한 가장 작은 단위를 말한다.
    - [ ] 상품의 이름은 빈 값이 될 수 없다.
    - [ ] 상품의 가격은 0 이상의 정수여야 한다.
    - [ ] 상품의 가격은 변경이 가능하다.
    - [ ] 상품의 가격이 변경될 때 해당 상품이 포함된 메뉴의 가격도 변경되어야 한다.
    - [ ] 모든 상품 조회가 가능해야 한다.

- 주문 테이블
    - [ ] 손님이 앉아서 식사를 할 수 있는 테이블 당 주문 현황을 나타낸다.
    - [ ] 각 테이블은 고유한 값으로 구분되며, 테이블명 및 앉은 손님의 수, 빈 테이블 여부를 갖는다.
    - [ ] 테이블을 생성할 수 있다.
    - [ ] 테이블의 이름은 빈 값이 될 수 없다.
    - [ ] 테이블은 빈 테이블 상태로 생성된다.
    - [ ] 테이블에 앉을 수 있는 손님의 수는 제한되어 있지 않다.
    - [ ] 테이블의 상태는 빈 테이블, 사용 중인 테이블인 상태를 가지며 변경이 가능하다.
    - [ ] 빈 테이블로 상태를 변경하면 손님 수도 0으로 조정해야 한다.
    - [ ] 사용 중 상태인 테이블은 손님 수를 변경할 수 있으며 손님 수는 0 이상이어야 한다.
    - [ ] 모든 주문 테이블 조회가 가능해야 한다. 

- 주문
    - [ ] 테이블에 앉은 손님의 주문 및 주문 상태를 나타낸다.
    - [ ] 주문을 생성할 수 있다.
    - [ ] 주문은 '배달', '포장', '홀식사'로 구분되며 구분값은 반드시 가져야 한다.
    - [ ] 주문엔 주문할 메뉴가 반드시 포함되어야 한다.
    - [ ] 주문할 메뉴는 가게에 이미 등록되어 있어야 하며 판매 중인 메뉴여야 한다.
    - [ ] 주문 메뉴의 가격은 가게에 등록된 메뉴의 가격과 같아야 한다. 
    - [ ] '홀식사' 주문이 아닌 경우 주문 메뉴가 0개 이상이어야 한다. 
    - [ ] 주문은 '대기', '수락', '조리', '배달중', '배달완료', '주문완료' 상태 중 하나를 갖는다.
    - [ ] 주문 등록 시 '대기' 상태로 등록되며 현재 시간이 함께 기록된다.
    - [ ] '배달' 주문은 배달주소가 반드시 포함되어야 한다.
    - [ ] '홀식사' 주문은 주문 테이블 정보가 반드시 포함되어야 한다.
    - [ ] '대기' 상태의 주문을 '수락' 상태로 변경할 수 있다. 배달 주문인 경우 배달기사를 부른다.
    - [ ] '수락' 상태의 주문을 '조리' 상태로 변경할 수 있다.
    - [ ] 배달 주문은 '조리' 상태의 주문을 '배달중' 상태로 변경할 수 있다.
    - [ ] 배달 주문은 '배달중' 상태의 주문을 '배달완료' 상태로 변경할 수 있다.
    - [ ] 배달 주문은 '배달완료' 상태의 주문을 '주문완료' 상태로 변경할 수 있다.
    - [ ] 포장 주문은 '조리' 상태의 주문을 '주문완료' 상태로 변경할 수 있다.
    - [ ] 홀식사 주문은 '조리' 상태의 주문을 '주문완료' 상태로 변경할 수 있으며 손님이 앉은 테이블은 빈 상태로 변경된다.
    - [ ] 모든 주문 조회가 가능해야 한다.
  
## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
