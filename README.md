# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항

- 치킨 가게에서 메뉴와 주문을 관리할 수 있는 pos 시스템을 구현한다.
- 상품
  - [ ]  가격, 이름을 가진 상품을 등록할 수 있다.
      - [ ]  가격이 null 또는 0 이하면 IllegalArgumentException 를 던진다.
      - [ ]  등록시 이름은 null 또는 빈값이면 IllegalArgumentException 를 던진다.
      - [ ]  상품 id값은 UUID를 기반으로 중복없이 랜덤하게 생성한다.

  - [ ]  상품의 가격을 수정할 수 있다.
     - [ ]  가격이 null 또는 0 이하면 IllegalArgumentException 를 던진다.
     - [ ]  수정하려는 상품ID가 조회되지 않는 경우 NoSuchElementException을 호출한다.
     - [ ]  수정하려는 상품이 포함된 메뉴의 기존 가격이 변경된 상품의 가격과 갯수를 곱한 합보다 클 경우 메뉴의 상태를 비노출 처리한다.
     
  - [ ]  등록된 상품 목록을 조회 할 수 있다.

- 메뉴그룹
    - [ ]  이름을 가진 메뉴 그룹을 등록할 수 있다.
        - [ ]  등록시 이름은 null 또는 빈값이면 IllegalArgumentException 를 던진다.
        - [ ]  메뉴그룹 id값은 UUID를 기반으로 중복없이 랜덤하게 생성한다.

    - [ ]  등록된 메뉴 그룹 목록을 조회 할 수 있다.

- 메뉴
    - [ ]  가격, 노출여부, 이름, 메뉴그룹 id를 가진 메뉴를 등록 할 수 있다.
        - [ ]  메뉴를 등록하기 위해서는 메뉴그룹이 우선 등록되어 있어야한다.
        - [ ]  메뉴 등록시 상품과 매핑되는 메뉴상품정보도 함께 등록된다. 
        - [ ]  가격이 null 또는 0 이하면 IllegalArgumentException 를 던진다.
        - [ ]  메뉴그룹 ID가 조회되지 않는 경우 NoSuchElementException 던진다. 
        - [ ]  메뉴에 등록하려는 상품의 갯수와 등록된 상품의 갯수가 일치하지 않으면 IllegalArgumentException를 던진다. 
        - [ ]  메뉴상품은 상품과 메뉴를 매핑한 정보이다.
        - [ ]  등록하려는 메뉴상품의 갯수가 0 이하이면 IllegalArgumentException를 던진다.
        - [ ]  상품 id가 조회되지 않는 경우 NoSuchElementException를 던진다.
        - [ ]  메뉴의 가격이 상품의 가격과 메뉴상품의 갯수를 곱한 값 보다 큰 경우 IllegalArgumentException 던진다.
        - [ ]  메뉴 id값은 UUID를 기반으로 중복없이 랜덤하게 생성한다.
        - [ ]  메뉴이름은 purgomalum(외부서비스)을 활용해서 검증한다.
        - [ ]  메뉴 이름이 null 또는 욕설이 있는 경우 IllegalArgumentException을 던진다.

    - [ ]  메뉴의 가격을 수정할 수 있다.
        - [ ]  수정하려는 메뉴의 가격이 null이거나 0보다 작은 경우 IllegalArgumentException 던진다.
        - [ ]  수정하려는 메뉴id가 조회되지 않는 경우 NoSuchElementException 던진다.
        - [ ]  수정하려는 메뉴의 가격이 상품의 가격과 메뉴상품의 갯수를 곱한 값 보다 큰 경우 IllegalArgumentException 던진다.

    - [ ]  메뉴의 상태는 활성화, 비활성화로 구분된다.
    - [ ]  메뉴의 상태를 비활성화 -> 활성화로 수정 할 수 있다.
        - [ ]  활성화 하려는 메뉴id가 조회되지 않는 경우 NoSuchElementException 던진다.
        - [ ]  활성화 하려는 메뉴의 가격이 상품의 가격과 메뉴상품의 갯수를 곱한 값 보다 큰 경우 IllegalArgumentException 던진다.
    - [ ]  메뉴의 상태를 활성화 -> 비활성화로 수정 할 수 있다.
        - [ ] 비 활성화 하려는 메뉴id가 조회되지 않는 경우 NoSuchElementException 던진다. 
    - [ ]  등록된 메뉴 목록을 조회 할 수 있다.

- 주문 테이블
    - [ ]  이름, 인원수, 점유여부의 정보를 가진 주문 테이블을 등록 할 수 있다.
        - [ ]  이름이 null 또는 0 이하면 IllegalArgumentException 를 던진다.
        - [ ]  주문테이블 id값은 UUID를 기반으로 중복없이 랜덤하게 생성한다.
        - [ ]  등록시 점유여부는 false, 인원수는 0으로 등록된다.
    - [ ]  주문테이블의 점유여부를 변경할 수 있다.(비점유 -> 점유)
        - [ ]  변경하려는 주문테이블 id가 없는 경우 NoSuchElementException 던진다.
        - [ ]  점유여부 값을 true로 변경한다.
    - [ ]  주문테이블의 점유여부를 변경할 수 있다.(점유 -> 비점유)
        - [ ]  변경하려는 주문테이블 id가 없는 경우 NoSuchElementException 던진다.
        - [ ]  점유여부 값은 false, 인원수는 0으로 변경된다.
        - [ ]  주문정보에서 주문테이블 정보가 존재하는데 주문상태가  completed 가 아닌경우 IllegalStateException 던진다.
    - [ ]  주문테이블의 인원수를 변경할 수 있다.
        - [ ]  변경요청한 인원수가 0 이하인 경우 IllegalArgumentException 던진다.
        - [ ]  수정하려는 주문테이블 id가 존재하지 않는 경우 NoSuchElementException 던진다.
        - [ ]  수정하려는 주문테이블이 점유되지 않았다면 IllegalStateException를 던진다. 
    - [ ]  등록된 주문테이블 목록을 조회 할 수 있다.
- 주문
    - [ ] 주문타입, 주문상태, 주문시간, 배달주소, 주문 테이블정보를 가진 주문을 등록할 수 있다.
        - [ ] 	배달주소와 주문테이블정보는 nullable이다.
        - [ ] 	주문 타입값은 DELIVERY, TAKEOUT, EAT_IN 중에 하나로 선택할 수 있다.
        - [ ] 	주문 상태는 WAITING, ACCEPTED, SERVED, DELIVERING, DELIVERED, COMPLETED로 구분된다.
        - [ ] 	주문의 타입값은 null이면 IllegalArgumentException 던진다.
        - [ ] 	주문등록시 주문메뉴와 갯수를 알 수 있는 order_line_item 값도 등록한다.
        - [ ] 	요청된 order_line_item 데이터가 없거나 null인 경우 IllegalArgumentException 던진다.
        - [ ] 	등록된 메뉴의 갯수와 등록하려는 주문메뉴 갯수가 불일치하면 IllegalArgumentException 던진다.
        - [ ] 	주문 타입이 EAT_IN이 아닌데 주문메뉴 양이 0보다 작으면 IllegalArgumentException 던진다.
        - [ ] 	등록하려는 주문메뉴에서 등록된 메뉴 정보가 없는 경우 NoSuchElementException 던진다.
        - [ ] 	등록된 메뉴의 노출여부값이 false인 경우 IllegalStateException 던진다.
        - [ ] 	등록된 메뉴의 가격과 등록하려는 주문메뉴의 가격이 불일치하면 IllegalArgumentException 던진다.
        - [ ] 	주문 id값은 UUID를 기반으로 중복없이 랜덤하게 생성한다.
        - [ ] 	등록시 주문상태는 WAITING, 주문시간은 현재시간으로 등록된다.
        - [ ] 	주문타입이 DELIVERY 인 경우 배달주소값이 있어야한다.
             - [ ] null이거나 빈값이면 IllegalArgumentException 던진다.
        - [ ] 	주문타입이 EAT_IN인 경우 주문테이블 정보가 있어야한다.
             - [ ] 주문테이블 정보가 없는 경우 NoSuchElementException을 던진다.
             - [ ] 주문테이블의 점유상태가 false이면 IllegalStateException 던진다.

    - [ ] 주문상태를 ACCEPTED로 변경할 수 있다.
        - [ ] 	변경하려는 주문 id 값이 없는 경우 NoSuchElementException 던진다.
        - [ ] 	기존 주문상태가 WAITING 아닌경우 IllegalStateException 던진다.
        - [ ] 	주문타입이 DELIVERY 인 경우 주문id, 총 주문 금액, 배달 주소를 라이더스 서비스(외부서비스)에 전달한다.

    - [ ] 주문상태를 SERVED로 변경할 수 있다.
        - [ ] 	변경하려는 주문 id 값이 없는 경우 NoSuchElementException 던진다.
        - [ ] 	기존 주문상태가 ACCEPTED 아닌경우 IllegalStateException 던진다.

    - [ ] 주문상태를 DELIVERING로 변경할 수 있다.
        - [ ] 	변경하려는 주문 id 값이 없는 경우 NoSuchElementException 던진다.
        - [ ] 	주문타입이 DELIVERY가 아닌경우 IllegalStateException 던진다.
        - [ ] 	기존 주문상태가 SERVED 아닌경우 IllegalStateException 던진다.
    
    - [ ] 주문상태를 DELIVERED로 변경할 수 있다.
        - [ ] 	변경하려는 주문 id 값이 없는 경우 NoSuchElementException 던진다.
        - [ ] 	주문타입이 DELIVERY가 아닌경우 IllegalStateException 던진다.
        - [ ] 	기존 주문상태가 DELIVERING 아닌경우 IllegalStateException 던진다. 

    - [ ] 주문상태를 COMPLETED로 변경할 수 있다.
        - [ ] 	변경하려는 주문 id 값이 없는 경우 NoSuchElementException 던진다.
        - [ ] 	주문타입이 DELIVERY인 경우
             - [ ] 기존 주문상태가 DELIVERING 아닌경우 IllegalStateException 던진다.
        - [ ] 	주문타입이 TAKEOUT 또는 EAT_IN인 경우
             - [ ] 기존 주문상태가 SERVED 아닌경우 IllegalStateException 던진다.
        - [ ] 	주문타입이 EAT_IN인 경우
             - [ ] 주문에 매핑된 주문테이블의 인원수를 0으로, 점유여부는 false로 수정한다.
    - [ ] 등록된 주문 목록을 조회 할 수 있다.
## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
