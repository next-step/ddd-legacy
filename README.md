# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항
- 메뉴그룹
  - [ ] 여러개의 메뉴들을 하나의 메뉴그룹으로 관리할 수 있다. ( 메뉴그룹 : 메뉴 = 1 : N 관계 )
  - [ ] 메뉴 그룹을 등록할 수 있다.
    - [ ] 메뉴 그룹 ID는 고유한 UUID를 생성해서 사용한다.
    - [ ] 메뉴그룹명은 필수이며 최대 255자까지 입력할 수 있다.
  - [ ] 메뉴 그룹 목록을 조회할 수 있다.
  
- 메뉴
  - [ ] 메뉴는 메뉴ID(UUID), 이름, 가격, 메뉴그룹, 노출/숨김여부, 메뉴구성상품목록 정보를 관리한다.
    - [ ] 메뉴ID는 고유한 UUID를 생성해서 사용한다.
    - [ ] 메뉴의 이름을 관리한다.
    - [ ] 메뉴의 가격정보를 관리한다.
    - [ ] 메뉴의 노출 / 숨김 여부를 관리한다.
    - [ ] 메뉴에 포함된 구성상품 목록을 관리한다.
  - [ ] 메뉴를 등록할 수 있다.
    - [ ] 메뉴를 등록할때 메뉴이름, 메뉴그룹ID, 가격, 구성상품정보를 필수로 입력해야한다. 
      - [ ] 메뉴이름은 공백/욕설을 제외한 최대 255자 이내로 입력한다.
      - [ ] 가격은 0원 이상으로 입력한다.
      - [ ] 메뉴그룹 ID를 필수로 입력한다.
      - [ ] 메뉴구성상품은 최소 1개 이상 입력한다.
        - [ ] 메뉴구성상품은 상품ID와 수량정보를 필수로 입력해야한다.
        - [ ] 상품ID는 DB에 등록된 상품의 ID를 입력한다.
        - [ ] 수량은 0 이상 값을 입력한다.
        - [ ] 메뉴의 가격과 메뉴구성상품의 총 금액이 일치해야한다.
  - [ ] 메뉴 가격을 변경할 수 있다.
    - [ ] 변경하려는 가격은 0원 이상이어야 한다.
    - [ ] 계산한 메뉴구성상품들의 총 합 금액과 사용자가 입력한 금액(Price)가 일치해야한다. 
  - [ ] 메뉴를 노출하거나 숨길 수 있다.
    - [ ] 계산한 메뉴구성상품들의 총 합 금액과 메뉴에 등록된 금액(price)가 같으면 노출처리한다.
    - [ ] 숨김처리는 등록된 메뉴인지 확인 후 숨김처리한다.
  - [ ] 메뉴 목록을 조회할 수 있다.

- 상품
  - [ ] 상품은 상품ID(UUID), 상품이름, 가격정보를 관리한다.
    - [ ] 상품ID는 고유한 UUID를 생성해서 사용한다.
  - [ ] 상품을 등록할 수 있다.
    - [ ] 가격은 0원 이상으로 입력한다.
    - [ ] 상품이름은 공백/욕설을 제외한 최대 255자 이내로 입력한다.
  - [ ] 상품의 가격을 변경할 수 있다.
    - [ ] 변경하려는 가격은 0원 이상으로 입력한다.
  - [ ] 상품목록을 조회할 수 있다.

- 주문
  - [ ] 주문을 생성할 수 있다.
    - [ ] 주문유형, 주문상품목록(메뉴ID, 가격, 수량) 정보를 필수로 입력한다.
    - [ ] 주문유형이 "배달(DELIVERY)"인 경우 배송지 주소를 필수로 입력한다.
    - [ ] 주문유형이 "식당에서(EAT_IN)"인 경우 주문테이블 번호를 필수로 입력한다. 
      - [ ] 주문테이블 번호는 등록된 테이블번호를 사용한다.
      - [ ] 주문테이블 상태가 "사용중(occupied)" 상태여야 한다.
    - [ ] 주문상품목록의 메뉴는 현재 공개된(displayed) 메뉴만 주문할 수 있다.
    - [ ] 주문상품목록에 입력된 메뉴 금액과 메뉴정보에 등록된 금액이 같은 경우에만 주문할 수 있다.
    - [ ] 주문유형이 "식당에서(EAT_IN)"가 아닌경우 주문수량은 0보다 크거나 같아야한다.
    - [ ] 주문을 등록할때 주문상태는 "접수대기(WAITING)" 상태로 등록한다.
  - [ ] 주문을 접수할 수 있다.
    - [ ] 등록된 주문번호가 있어야 주문상태를 변경할 수 있다.
    - [ ] 주문 상태가 "접수대기(WAITING)"상태인 경우에만 접수가 가능하다.
    - [ ] 주문유형이 "배달(DELIVERY)"인 경우 배달요청을 한다.
      - [ ] 배달요청을 할때는 주문번호, 총 주문금액, 배달주소를 입력한다.
    - [ ] 주문상태를 "접수(ACCEPTED)" 상태로 변경한다.
  - [ ] 주문을 서빙할 수 있다.
    - [ ] 등록된 주문번호가 있어야 주문상태를 변경할 수 있다.
    - [ ] 주문상태가 "접수(ACCEPTED)" 상태인 경우에만 "서빙완료(SERVED)" 상태로 변경할 수 있다.
  - [ ] 주문 배달을 시작할 수 있다.
    - [ ] 등록된 주문번호가 있어야 주문상태를 변경할 수 있다.
    - [ ] 주문유형이 "배달(DELIVERY)"인 경우에만 주문배달 상태로 변경할 수 있다.
    - [ ] 주문상태가 "서빙완료(SERVED)"인 경우에만 "배달 진행중(DELIVERING)"상태로 변경할 수 있다.
  - [ ] 주문 배달을 완료할 수 있다.
    - [ ] 등록된 주문번호가 있어야 주문상태를 변경할 수 있다.
    - [ ] 주문상태가 "배달 진행중(DELIVERING)"인 경우에만 "배달완료(DELIVERED)"상태로 변경할 수 있다.
  - [ ] 주문을 완료할 수 있다.
    - [ ] 등록된 주문번호가 있어야 주문상태를 변경할 수 있다.
    - [ ] 주문유형이 "배달(DELIVERY)"인 경우 주문상태가 "배달완료(DELIVERED)"인 경우에만 "주문완료(COMPLETED)" 상태로 변경할 수 있다.
    - [ ] 주문유형이 "포장(TAKEOUT), 식당에서(EAT_IN)"인 경우 "서빙완료(SERVED)" 상태인 경우에만 "주문완료(COMPLETED)" 상태로 변경할 수 있다.
    - [ ] 주문상태를 "주문완료(COMPLETED)" 상태로 변경한다.
    - [ ] 주문유형이 "식당에서(EAT_IN)"인 경우 주문테이블을 정리한다.
      - [ ] 사용중 상태를 미사용 상태로 변경한다.
      - [ ] 착석인원을 0으로 변경한다.
  - [ ] 주문목록을 조회할 수 있다.
  

 # 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
