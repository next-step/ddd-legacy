# 키친포스

## 요구 사항

- 간단한 POS 를 구현한다.
- 메뉴 그룹
    - [x] 메뉴 그룹을 생성한다.
        - [x] 이름은 빈 값이 아니어야 한다.
        - [x] 식별자를 생성한다.
    - [x] 전체 메뉴 그룹을 조회한다.
- 메뉴
    - [x] 메뉴를 생성한다.
        - [x] 가격은 0 보다 작을 수 없다.
        - [x] 유효한 메뉴 그룹을 설정해야 한다.
        - [x] 메뉴 상품은 1 개 이상이어야 한다.
        - [x] 메뉴 상품은 모두 유효하게 상품으로 등록되어 있어야 한다.
        - [x] 메뉴 상품의 수량은 0 보다 작을 수 없다.
        - [x] 메뉴의 가격은 포함된 메뉴 상품들의 (가격 * 수량) 합을 넘을 수 없다.
        - [x] 이름은 빈 값이 아니어야 한다.
        - [x] 이름은 비속어가 아니어야 한다.
        - [x] 식별자를 생성한다.
    - [x] 메뉴의 가격을 변경한다.
        - [x] 가격은 0 보다 작을 수 없다.
        - [x] 가격은 포함된 메뉴 상품들의 (가격 * 수량) 합을 넘을 수 없다.
    - [x] 메뉴를 전시한다.
        - [x] 가격이 포함된 메뉴 상품들의 (가격 * 수량) 합을 넘지 않는 경우에만 전시한다.
    - [x] 메뉴를 숨긴다.
    - [x] 전체 메뉴를 조회한다.
- 주문 테이블
    - [x] 주문 테이블을 생성한다.
        - [x] 식별자를 생성한다.
        - [x] 이름을 설정한다.
        - [x] 손님 수를 0 명으로 설정한다.
        - [x] 빈 상태로 설정한다.
    - [x] 주문 테이블에 앉는다.
        - [x] 비어있지 않은 상태로 설정한다.
    - [ ] 주문 테이블을 비운다.
        - [x] 손님 수를 0 명으로 설정한다.
        - [x] 빈 상태로 설정한다.
        - [ ] 주문 테이블에 주문 상태가 완료되지 않은 주문이 있을 경우, 비울 수 없다. 
    - [x] 주문 테이블에 손님 수를 변경한다.
        - [x] 손님 수는 0 보다 작을 수 없다.
        - [x] 손님 수를 변경한다.
    - [x] 전체 주문 테이블을 조회한다.
- 주문
    - [x] 주문한다.
        - [x] 주문의 종류는 배달, 포장, 매장 식사 3 가지로 가능하다.
        - [x] 주문 항목
            - [x] 주문 항목은 1 개 이상이어야 한다.
            - [x] 주문 항목은 메뉴에도 있어야 한다.
            - [x] 매장 식사가 아닌 경우, 주문 항목의 수량은 0 보다 작을 수 없다.
            - [x] 주문 항목에 해당하는 메뉴가 모두 전시 상태여야 한다.
            - [x] 주문 항목의 가격과 해당하는 메뉴의 가격은 같아야 한다.
        - [x] 식별자를 생성한다.
        - [x] 주문 상태는 대기중으로 설정한다.
        - [x] 주문 날짜는 현재로 설정한다.
        - [x] 주문 종류
            - [x] 배달
                - [x] 배달 주소가 빈 값일 수 없다.
            - [x] 매장에서 식사
                - [x] 주문 테이블이 비어있지 않아야 한다.
    - [x] 주문을 수락한다.
        - [x] 주문 상태는 대기중이어야 한다.
        - [x] 주문 종류가 배달일 경우, 아래의 정보를 라이더스로 전달한다.
            - [x] 주문 식별자
            - [x] 주문 항목들의 (가격 * 수량) 합
            - [x] 배달 주소
        - [x] 주문 상태를 수락됨으로 변경한다.
    - [x] 주문을 서빙한다.
        - [x] 주문 상태는 수락됨이어야 한다.
        - [x] 주문 상태를 서빙됨으로 변경한다.
    - [x] 주문을 배달한다.
        - [x] 주문 종류는 배달이어야 한다.
        - [x] 주문 상태는 서빙됨이어야 한다.
        - [x] 주문 상태를 배달중으로 변경한다.
    - [x] 주문 배달을 완료한다.
        - [x] 주문 종류는 배달이어야 한다.
        - [x] 주문 상태는 배달중이어야 한다.
        - [x] 주문 상태를 배달됨으로 변경한다.
    - [ ] 주문을 완료한다.
        - [ ] 주문 종류가 배달이면, 주문 상태가 배달됨이어야 한다.
        - [ ] 주문 종류가 포장이나 매장식사이면, 주문 상태가 서빙됨이어야 한다.
        - [ ] 주문 상태를 완료로 변경한다.
        - [ ] 주문 종류가 매장식사인 경우, 주문 테이블을 아래 상태로 변경한다.
            - [ ] 손님 수 0명
            - [ ] 빈 상태
    - [ ] 전체 주문을 조회한다.
- 상품
    - [x] 상품을 생성한다.
        - [x] 가격은 0 보다 작을 수 없다.
        - [x] 이름은 빈 값이 아니어야 한다.
        - [x] 이름은 비속어가 아니어야 한다.
        - [x] 식별자를 생성한다.
    - [x] 상품의 가격을 변경한다.
        - [x] 가격은 0 보다 작을 수 없다.
        - [x] 상품이 포함되어 있는 모든 메뉴에 대해서 메뉴 가격이 각 포함된 메뉴 상품들의 (가격 * 수량) 합을 넘는 경우 노출하지 않는다.
    - [x] 전체 상품을 조회한다.

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
