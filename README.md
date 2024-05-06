# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항

- ```kitchenpos(키친포스)``` 요구 사항을 정리한다.

#### 메뉴

- 메뉴 정보에는 이름, 가격, 그룹정보, 노출여부, 상품상세정보가 있다.
- 신규 등록
    - [ ] 메뉴 이름
        - [ ] 메뉴 이름은 필수로 입력해야한다.
        - [ ] 메뉴 이름에 욕설이 포함되어있는 경우 등록할 수 없다.
    - [ ] 메뉴 금액
        - [ ] 메뉴마다 금액정보는 필수로 입력해야한다.
        - [ ] 0원보다 적은 금액을 입력하는 경우 메뉴를 등록할 수 없다.
    - [ ] 메뉴그룹정보
        - [ ] 하나의 메뉴 그룹 정보가 필수로 입력해야한다.
        - [ ] 등록되어있지 않은 그룹정보를 입력하는 경우 메뉴를 등록할 수 없다.
    - [ ] 노출 여부
        - [ ] 메뉴 노출여부를 처음에 결정할 수 있다.
        - [ ] 노출여부는 필수로 입력해야한다.
    - [ ] 상품상세정보
        - [ ] 메뉴를 구성하는 상품정보를 필수로 입력해야한다.
        - [ ] 상품정보는 하나 이상 입력이 가능하다.
        - [ ] 등록되어있지 않은 상품정보를 입력하는 경우 메뉴를 등록할 수 없다.
        - [ ] 상품정보의 수량이 0원보다 작은 경우 메뉴를 등록할 수 없다.
        - [ ] 상품정보의 총 합계 금액보다 메뉴의 가격이 비싼 경우 등록할 수 없다.
- 금액 변경
    - [ ] 메뉴는 금액을 변경할 수 있다.
    - [ ] 변경할 금액 정보가 없는 경우 변경이 불가하다.
    - [ ] 0원보다 작은 금액을 입력하는 경우 변경이 불가하다.
    - [ ] 변경하려고 하는 메뉴 정보가 없는 경우 변경이 불가하다.
    - [ ] 변경금액이 상품 총 합계 금액보다 비싼 경우 변경할 수 없다.
- 노출여부 변경
    - [ ] 메뉴는 노출여부를 변경할 수 있다.
    - 노출로 변경
        - [ ] 등록된적 없는 메뉴는 변경이 불가하다.
        - [ ] 노출하려는 메뉴의 금액이 상품정보의 총 합계 금액보다 높은 경우 변경할 수 없다.
    - 미노출로 변경
        - [ ] 등록된적 없는 메뉴는 변경이 불가하다.
- 전체 조회
    - [ ] 등록되어 있는 모든 메뉴를 조회할 수 있다.

#### 메뉴그룹

- 등록
    - [ ] 메뉴 그룹의 이름은 필수로 입력해야한다.
    - [ ] 빈값만 입력하는 경우 등록할 수 없다.
- 전체 조회
    - [ ] 등록되어 있는 전체 메뉴 그룹을 조회할 수 있다.

#### 상품

- 등록
    - [ ] 싱픔의 가격은 필수로 입력해야한다.
    - [ ] 0개 미만으로 입력하는 경우 등록할 수 없다.
    - [ ] 상품 이름을 입력하지 않는 경우 등록할 수 없다.
    - [ ] 상품 이름에 욕설이 포함되어있는 경우 등록할 수 없다.
- 가격 변경
    - [ ] 상품 가격은 변경할 수 있다.
    - [ ] 변경 가격을 입력하지 않는 경우 변경할 수 없다.
    - [ ] 0원보다 적게 입력하는 경우 변경할 수 없다.
    - [ ] 등록되어 있지 않은 상품 정보인 경우 변경할 수 없다.
    - [ ] 금액 변경으로 인해 해당 상품이 포함된 메뉴의 가격이 메뉴 구성 전체 상품의 총 금액보다 비싸지는 경우 변경할 수 없다.
- 전체 조회
    - [ ] 등록되어 있는 전체 상품을 조회할 수 있다.

#### 주문

- 주문접수 (WAITING)
    - 주문접수를 받을 때에는 주문타입, 주문테이블, 메뉴정보가 있다.
    - 주문타입
        - [ ] 주문타입 정보가 없는 경우 주문을 받을 수 없다.
        - 포장
            - [ ] 주문한 메뉴 중 0개보다 작은 메뉴가 있는 경우 주문할 수 없다.
        - 배달 주문
            - [ ] 배달 주소 정보는 필수로 입력해야한다.
            - [ ] 주문한 메뉴 중 0개보다 작은 메뉴가 있는 경우 주문할 수 없다.
        - 매장식사
            - [ ] 주문 테이블 정보가 필요하다.
            - [ ] 등록된 테이블정보가 아닌 경우 접수가 불가하다.
            - [ ] 손님이 착석중이 아닌 테이블로 접수가 들어온 경우 접수가 불가능하다.
    - 메뉴
        - [ ] 하나의 주문에 여러개 메뉴 주문이 가능하다.
        - [ ] 메뉴를 하나도 선택하지 않은 경우 주문할 수 없다.
        - [ ] 주문한 메뉴들 중에 하나라도 등록되어 있지 않은 메뉴가 포함되어 있으면 주문할 수 없다.
        - [ ] 미노출된 메뉴가 포함되어있는 경우 주문할 수 없다.
        - [ ] 주문한 메뉴의 금액과 등록된 메뉴 금액이 다른 경우 주문할 수 없다.
    - [ ] 주문이 접수완료된 경우 주문상태가 WAITING 으로 저장된다.
- 주문수락 (ACCEPTED)
    - [ ] 접수된적 없는 주문인 경우 수락이 불가능하다.
    - [ ] 주문상태가 대기중이 아닌 경우 처리가 불가능하다.
    - [ ] 배달주문인 경우 배달라이더에게 배달 요청이 필요하다.
    - [ ] 라이더에게 요청하는 경우 주문 정보, 총금액, 배달주소 정보를 제공해야한다.
    - [ ] 주문수락이 완료된 경우 주문상태가 WAITING 에서 ACCEPTED 로 변경된다.
- 음식 제공 (SERVED)
    - [ ] 접수된적 없는 주문인 경우 처리할 수 없다.
    - [ ] 주문상태가 ACCEPTED가 아닌 경우 처리할 수 없다.
    - [ ] 처리가 되는경우 주문상태가 ACCEPTED 에서 SERVED 로 변경된다.
- 배달 시작 (DELIVERING)
    - [ ] 접수된적 없는 주문인 경우 배달을 시작할 수 없다.
    - [ ] 주문상태가 DELIVERY가 아닌 경우 배달을 시작할 수 없다.
    - [ ] 주문상태가 SERVED가 아닌 경우 배달을 시작할 수 없다.
    - [ ] 배달 시작상태가 되는 경우 SERVED에서 DELIVERING 로 변경된다.
- 배달 완료 (DELIVERED)
    - [ ] 접수된적 없는 주문인 경우 처리할 수 없다.
    - [ ] 주문상태가 DELIVERING이 아닌 경우 배달완료로 처리할 수 없다.
    - [ ] 배달완료상태가 되는 경우 DELIVERING 에서 DELIVERED 로 변경된다.
- 주문 완료 (COMPLETED)
    - [ ] 접수된적 없는 주문인 경우 처리할 수 없다.
    - [ ] 배달접수인 경우 DELIVERED 상태가 아니면 처리할 수 없다.
    - [ ] 포장주문이거나 매장식사인 경우 SERVED 상태가 아니면 처리할 수 없다.
    - [ ] 완료상태가되면 COMPLETED 가 된다.
    - [ ] 메징식사인 경우 주문상태가 완료인 경우 주문테이블의 손님수와 착석여부 상태가 초기화된다. (손님 수 0명, 미착석 상태)
- 전체 조회
    - [ ] 접수되었던 모든 주문 정보를 조회할 수 있다.

#### 주문테이블

- 테이블정보에는 테이블이름, 손님 수, 착석여부가 있다.
- 테이블정보 등록
    - [ ] 테이블 이름은 필수로 입력해야한다.
    - [ ] 최초 등록시 테이블의 손님수, occupied 상태는 모두 초기화된다.
- 테이블 착석여부 변경
    - 착석중
        - [ ] 등록된 테이블정보가 아닌 경우 처리가 불가능하다.
        - [ ] 착석여부 상태가 착석으로 변경된다.
    - 미착석
        - [ ] 등록된 테이블정보가 아닌 경우 처리가 불가능하다.
        - [ ] 테이블에 주문된적이 있고 주문상태가 COMPLETED 가 아닌 경우 처리가 불가능하다.
        - [ ] 손님 수 정보와 착석여부 상태가 미착석으로 변경된다.
- 손님수 변경
    - [ ] 변경하려는 손님 수가 0명보다 적은 경우 변경할 수 없다.
    - [ ] 등록된 테이블 정보가 아닌 경우 처리가 불가능하다.
    - [ ] 테이블이 손님 착성중 상태가 아닌 경우 처리가 불가능하다.
- 전체 조회
    - [ ] 등록된 모든 테이블 정보를 조회할 수 있다.

## 용어 사전

| 한글명      | 영문명          | 설명                                                                                                            |
|----------|--------------|---------------------------------------------------------------------------------------------------------------|
| 주문타입     | Order type   | 메뉴 주문의 종류를 뜻하며, 매장식사(eat-in), 배달(delivery), 포장주문(take-out)이 가능하다.                                             |
| 메뉴 그룹정보  | Menu group   | 메뉴를 묶어서 관리할 수 있는 그룹정보로 메뉴별로 하나의 그룹정보가 등록된다.                                                                   |
| 메뉴 노출여부  | Display      | 매장의 메뉴판 또는 배달앱에서 고객에게 메뉴 노출여부를 뜻한다.                                                                           |
| 상품       | Product      | 메뉴를 구성하고 있는 상품 정보를 의미한다.                                                                                      |
| 주문 상태    | Order status | 주문의 상태를 의미하며, 접수(WAITING), 수락(ACCEPTED), 음식제공(SERVED), 배달시작(DELIVERING), 배달완료(DELIVERED), 완료(COMPLETED)가 있다.) |
| 테이블 착석여부 | Occupied     | 테이블에 손님의 착석여부를 나타낸다.                                                                                          |

## 모델링

