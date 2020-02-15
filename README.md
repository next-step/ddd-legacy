# 키친포스

## 요구 사항
- 오프라인 매장인 키친 포스를 구현한다.

- 상품 (Products)
    - [ ] 상품을 등록 할 수 있다.
        - [ ] 상품은 이름과 가격으로 구성 된다.
        - [ ] 상품의 가격이 올바르지 않으면 등록할 수 없다.
    - [ ] 상품 목록을 조회 할 수 있다.

- 메뉴 그룹 (MenuGroups)
    - [ ] 메뉴 그룹을 등록 할 수 있다.
        - [ ] 메뉴 그룹은 이름으로 구성 된다.  
    - [ ] 메뉴 그룹 목록을 조회 할 수 있다.
         
- 메뉴 (Menu)
    - [ ] 메뉴를 등록 할 수 있다.
        - [ ] 메뉴의 가격이 올바르지 않으면 등록할 수 없다.
        - [ ] 메뉴 그룹이 존재 하지 않으면 등록할 수 없다. 
        - [ ] 메뉴의 가격이, 메뉴에 속한 상품 금액의 합 보다 크면 안된다. 
    - [ ] 메뉴를 조회 할 수 있다.
      
- 테이블 (Table)
    - [ ] 주문 테이블을 등록할 수 있다.
    - [ ] 주문 테이블 목록을 조회 할 수 있다.
    - [ ] 주문 테이블의 상태를 비움으로 업데이트 할 수 있다. 
        - [ ] 주문 테이블이 정상값 이어야 한다. 
        - [ ] 반드시 식사가 끝난 경우에 상태를 비움으로 업데이트 할 수 있다.
    - [ ] 특정 테이블의 인원수를 업데이트 한다.
        - [ ] 인원수는 정상값만 입력 받는다.
        - [ ] 주문 테이블이 반드시 존재해야 인원수 없데이트가 가능하다.
        
- 테이블 그룹 (Table Groups)
    - [ ] 테이블 그룹을 만들 수 있다.
    - [ ] 주문테이블이 1개 이상인 경우에 만들 수 있다.
    - [ ] 주문테이블의 사이즈와 동일해야 만들 수 있다.
    - [ ] 테이블 그룹을 삭제 할 수 있다.
    - [ ] 주문 상태가 요리중이거나 먹고있으면 삭제 할 수 없다. 

- 주문 (Order)
    - [ ] 주문을 할 수 있다.
    - [ ] 아이템이 존재해야 주문을 할 수 있다.
    - [ ] 주문한 아이템의 메뉴 개수와 메뉴DB에서 개수와 동일해야 한다.
    - [ ] 주문 테이블이 존재해야 한다.
    - [ ] 주문을 할때는 주문상태를 COOKING 으로 설정한다.  
    - [ ] 전체 주문 목록을 조회 할 수 있다.
    - [ ] 주문 상태를 업데이트 할 수 있다.
    - [ ] 주문 상태는 MEAL, COOKING, COMPLETION 으로 구성된다. 
    - [ ] 주문 번호가 반드시 존재해야지 업데이트 가능하다.
    - [ ] 주문의 상태가 완료되면 업데이트 할 수 없다.

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
| 상품 | Products | 메뉴에 들어갈 상품을 나타낸다. | 
| 메뉴 그룹 | MenuGroup | 메뉴들을 묶어서 표현한다. ex. 추천메뉴, 점심메뉴  | 
| 메뉴 | Menu | 메뉴를 나타낸다. 이름과 가격으로 구성되고, 메뉴그룹, 상품들로 연관관계를 갖는다. | 
| 테이블 | Table | 오프라인 매장에서 운영중인 테이블을 나타낸다. 테이블의 인원수와 빈 자리인지 여부를 나타 낸다. | 
| 테이블 그룹 | TableGroup | 주문한 테이블에 대한 정보를 나타낸다. | 
| 주문 | Order | 메뉴에있는 상품과 주문한 테이블 정보를 가지고 있고, 주문 상태 정보를 나타낸다. | 

## 모델링
![model](https://user-images.githubusercontent.com/28615416/74082848-761ca400-4aa1-11ea-809f-2dcbf016bbd7.png)