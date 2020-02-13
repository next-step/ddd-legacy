# 키친포스

## 요구 사항
- 
- 상품
    - [ ] 상품은 매장에서 판매한다. 
        - [ ] 상품이 판매될 때 이름이 있어야 한다.
        - [ ] 상품이 판매될 때 가격이 0원 이상이어야 한다.
        - [ ] 상품이 판매될 때 상품 이름이 중복이 될 수 없다.
    - [ ] 등록되어진 상품을 볼 수 있다.
    
- 메뉴 그룹
    - [ ] 메뉴 그룹은 카테고리 역할을 한다.
    - [ ] 메뉴 그룹은 메뉴판 상위에 노출이 된다. 
        - [ ] 노출이 될 때 메뉴 그룹 이름은 중복될 수 없다.
        - [ ] 노출이 될 때 메뉴 그룹 이름은 빈 값을 허용하지 않는다.
    - [ ] 등록되어진 메뉴 그룹을 볼 수 있다.
     
- 메뉴
    - [ ] 메뉴는 실제 고객에게 보여지는 상품(음식) 이다.
        - [ ] 메뉴 이름은 중복될 수 없다.
        - [ ] 메뉴 이름은 빈 값을 허용하지 않는다.
        - [ ] 메뉴 가격은 0원 이상이다.
        - [ ] 메뉴 가격은 빈 값을 허용하지 않는다.
    - [ ] 메뉴는 상위 카테고리를 갖는다.
        - [ ] 상위 카테고리를 가질 때 메뉴는 메뉴 그룹의 하위에 노출이 된다.
            - [ ] 하위에 노출이 될 때 상품들을 선택할 수 있다.
                - [ ] 상품들을 선택할 때 매장에 등록된 상품만 선택이 가능하다.
                - [ ] 메뉴 이름은 상품 이름과 다를 수 없다.
        - [ ] 메뉴는 메뉴 그룹에 따라 가격이 달라질 수 있다.             
    - [ ] 등록된 메뉴들을 볼 수 있다.
     
 
- 메뉴 상품
    - [ ] 메뉴 상품은 상품을 갖는다.
        - [ ] 상품을 가질 때 매장에 등록되어진 상품만 취급한다.
    - [ ] 메뉴 상품은 수량을 갖는다.
        - [ ] 수량은 최소 1개 이상이다.
    - [ ] 메뉴 상품을 조회할 수 있다.
    
- 주문
    - [ ] 주문은 메뉴를 통해 발급된다. 
        - [ ] 주문이 발급되어 질 때 아이템들을 갖는다.
            - [ ] 아이템을 가질 때 아이템들은 빈 값일 수 없다.
        - [ ] 메뉴와 아이템들의 값이 다를 수 없다.
    - [ ] 주문한 상품과 메뉴의 상품이 일치하는지 확인 한다.
    - [ ] 주문은 주문 테이블을 갖는다.
        - [ ] 주문 테이블을 가질 때 주문 테이블은 빈 값일 수 없다.
    - [ ] 주문은 요리중, 식사, 완료 상태 값을 갖는다.
    - [ ] 주문을 하게 되면 요리중의 상태 값을 갖는다.
    
- 주문 아이템 
    - [ ] 주문 아이템은 메뉴를 통해 주문 아이템을 등록한다.
        - [ ] 아이템이 등록되어질 때 주문 아이템은 빈 값일 수 없다. 
        - [ ] 주문 아이템의 메뉴가 전부 일치하는지 유효해야 한다.
    - [ ] 주문 아이템은 메뉴, 주문, 수량을 갖는다.
      
- 주문 테이블
    - [ ] 주문 테이블은 식당에 있는 자리이다.
    - [ ] 주문 테이블은 테이블이 비었는지 안비었는지 알 수 있다.
        - [ ] 주문 테이블이 비었다면 주문할 수 있으며 현재 자리에 있는 손님 수는 0명 이다.
        - [ ] 주문 테이블이 비어있지 않다면 주문할 수 없으며, 현재 자리에 있는 손님이 몇 명인지 알 수 있다.
        - [ ] 주문 테이블에 손님이 있는데, 같은 테이블에서 주문을 할 수 없다.
        - [ ] 주문 테이블이 비어있는데 손님이 앉아 있을 수 없다.

- 주문 테이블 그룹
    - [ ] 주문 테이블 그룹은 식당에서 두 자리 이상의 테이블이다.
    - [ ] 주문 테이블 그룹에서 두 자리 미만의 테이블을 사용할 수 없다.
    - [ ] 주문 테이블 그룹에서 없는 자리를 이용할 때 주문을 할 수 없다.
    - [ ] 주문하려는 자리들과 식당에 존재하는 자리의 수가 다를 때 주문을 할 수 없다.
    - [ ] 이용하려는 자리들이 비어있지 않다면 자리에 앉을 수 없다.
    - [ ] 주문 상태가 요리 중, 먹는 중일 때 주문 테이블을 정리 할 수 없다.
    
## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
