# 키친포스

## 요구 사항

- 메뉴 그룹
  - [ ] 메뉴 그룹을 생성한다
  - [ ] 메뉴 그룹들을 모두 조회한다
- 메뉴
  - [ ] 메뉴 그룹 안에 메뉴를 생성한다
  - [ ] 메뉴는 복수의 상품들로 구성될 수 있다.
  - [ ] 메뉴의 이름, 가격, 포스에 보여질 지 안보여질 지에 대한 여부를 결정하여 생성할 수 있다.
  - [ ] 메뉴의 가격을 수정할 수 있다
  - [ ] 메뉴가 포스기에 나타나게, 나타나지 않게 할 수 있다.
  - [ ] 전체 메뉴를 조회할 수 있다.
- 테이블
  - [ ] 방문객이 앉을 수 있는 테이블을 테이블명과 함께 생성할 수 있다.
  - [ ] 테이블에 앉은 상태로 수정이 가능하다.
  - [ ] 테이블에 앉은 고객의 수를 설정할 수 있다.
  - [ ] 테이블이 비워진 상태로 수정이 가능하다.
  - [ ] 전체 테이블을 조회할 수 있다.
- 주문
  - 주문 접수
    - [ ] 여러가지 메뉴를 선택하여 주문을 접수할 수 있다.
    (주문 타입: 현장식사, 배달, 포장 중 선택 가능)
    - [ ] 주문 종류가 정해지지 않았다면 주문을 할 수 없다. 
    - [ ] 주문한 메뉴가 없다면 주문 할 수 없다. 
    - [ ] 주문 요청이 들어온 메뉴들 중 등록되어 있지 않은 메뉴가 있다면 주문을 할 수 없다. 
    - [ ] 현장식사가 아닐 경우 메뉴 주문 수량이 음수이면 주문을 할 수 없다. 
    - [ ] 주문 요청이 들어온 메뉴가 전시 되어 있지 않은 메뉴라면 주문을 할 수 없다. 
    - [ ] 주문 요청에 포함된 가격이 메뉴의 가격과 다르다면 주문을 할 수 없다.
    - [ ] 주문 접수가 되고 수락이 되기를 기다려야한다. 
    - [ ] 배달 주문일 경우 배달 주소가 없다면 주문 할 수 없다. 
    - [ ] 현장식사 일 경우 요청한 테이블 번호가 등록되어 있지 않다면 주문을 할 수 없다.
    - [ ] 주문한 테이블을 지정할 수 있다.
  - [ ] 주문의 상태를 변경할 수 있다.
    (주문 수락, 서빙 완료, 배달 시장, 배달 종료, 주문 수행 완료)
  - 주문 수락
    - [ ] 주문 번호가 없다면 주문을 수락할 수 없다.
    - [ ] 주문 상태가 수락 대기중이 아니라면 주문을 수락할 수 없다.
    - [ ] 배달 주문은 수ㅜ락 시 주문 메뉴 가격을 합하여 주문번호, 배달 주소, 총 가격을 라이더에게 전달한다.
  - 서빙 완료
    - [ ] 주문 번호가 없다면 서빙할 수 없다.
    - [ ] 주문이 가게에 의해 수락되지 않았을 경우 서빙할 수 없다.
  - 배달 시작
    - [ ] 주문번호가 없다면 배달을 시작할 수 없다.
    - [ ] 배달 주문이 아니라면 배달을 시작할 수 없다.
    - [ ]서빙완료인 상태가 아니라면 배달을 시작할 수 없다.
  - 배달 종료
    - [ ] 주문번호가 없다면 배달을 종료할 수 없다.
    - [ ] 주문이 배달 중이 아니라면 배달을 종료할 수 없다.
  - [ ] 전체 주문을 조회할 수 있다.
- 상품
  - [ ] 상품을 저장할 수 있다.
  - [ ] 가격이 입력되지 않은 상품은 저장되지 않는다.
  - [ ] 가격이 음수로 입력되었을 경우 상품이 저장되지 않는다.
  - [ ] 상품명이 입력되지 않은 상품은 저장되지 않는다.
  - [ ] 상품명에 욕설이 포함 될 경우 상품은 저장되지 않는다.
  - [ ] 상품 가격을 수정할 수 있다.
  - [ ] 상품 가격이 입력되지 않을 경우 가격 수정이 불가하다.
  - [ ] 음수가 입력되었을 경우 가격 수정이 불가하다.
  - [ ] 가격 변경된 상품을 포함한 메뉴의 변경된 가격이 기존 가격보다 작을 경우 메뉴를 선택하지 못한다.
  - [ ] 전체 상품을 조회할 수 있다.

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
