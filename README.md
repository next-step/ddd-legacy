# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항
메뉴를 등록하고 주문받는 키친포스를 구현한다.
### 상품
- 상품을 등록할 수 있다.
  - 상품의 이름은 비어있거나 욕설이 포함되면 안 된다. 
  - 상품의 가격은 0원보다 커야 한다.
- 상품의 가격을 변경할 수 있다.
  - 상품의 가격은 0원보다 커야 한다.
  - 가격 변경 후 메뉴에 등록된 상품 가격의 합보다 메뉴 가격이 크면, 해당 메뉴는 메뉴판에 노출되지 않는다.
- 상품을 전체 조회할 수 있다.
  - 전체 상품의 이름과 가격을 조회한다.
  
### 메뉴
- 메뉴를 등록할 수 있다.
  - 메뉴는 하나의 메뉴 그룹에 포함되어야 한다.
  - 메뉴 이름은 비어있거나 욕설이 포함되면 안 된다.
  - 메뉴 가격은 0원보다 커야 한다.
  - 메뉴는 하나 이상의 상품을 포함해야 한다.
  - 메뉴에 각 상품이 몇 개 포함되었는지도 등록해야 한다.
  - 메뉴에 등록된 각 상품의 개수는 0개보다 많아야 한다.
  - 메뉴에 등록된 상품 가격의 합보다 메뉴 가격이 크면 안 된다.
  - 메뉴는 메뉴판에 노출되거나 제외될 수 있다.
- 메뉴 가격을 변경할 수 있다.
  - 메뉴 가격은 0원보다 커야 한다.
  - 메뉴에 등록된 상품 가격의 합보다 메뉴 가격이 크면 안된다.
- 메뉴를 메뉴판에 노출할 수 있다.
  - 메뉴에 등록된 상품 가격의 합보다 메뉴 가격이 크지 않을 경우에만 메뉴판에 노출한다.
- 메뉴를 메뉴판에서 제외할 수 있다.
  - 메뉴를 메뉴판에 노출하지 않는다.
- 메뉴를 전체 조회할 수 있다.
  - 전체 메뉴의 이름, 가격, 메뉴 그룹, 메뉴판 노출 여부, 상품, 각 상품의 수량을 조회한다.
###메뉴 그룹
- 메뉴 그룹을 등록할 수 있다.
  - 메뉴 그룹의 이름은 비어있거나 공백이면 안 된다.
- 메뉴 그룹을 전체 조회할 수 있다.
  - 전체 메뉴 그룹의 이름을 조회한다.
###주문 테이블
- 주문 테이블을 등록할 수 있다.
  - 테이블의 이름은 비어있으면 안 된다.
  - 테이블이 사용 중인지 비어있는지 입력한다.
  - 테이블에 손님이 몇 명 앉아있는지 입력한다.
- 테이블을 사용 중인 상태로 변경할 수 있다.
- 테이블을 비어있는 상태로 변경할 수 있다.
  - 테이블에서 주문을 했고, 주문이 완료 상태가 아닌 경우 상태를 변경할 수 없다. 
  - 테이블의 손님 수를 0명으로 변경한다.
- 손님 수를 변경할 수 있다.
  - 손님 수는 0명 이상이어야 한다.
  - 테이블이 사용 중인 경우에만 손님 수를 변경할 수 있다.
- 테이블을 전체 조회할 수 있다.
  - 전체 주문 테이블의 이름, 손님 수, 사용 여부를 조회한다.

### 주문
- 주문 타입은 배달, 테이크아웃, 매장 식사(DELIVERY, TAKEOUT, EAT_IN)가 있다.
- 주문을 등록할 수 있다.
  - 주문 타입을 입력한다.
  - 주문 등록 시, 주문을 등록한 시간이 저장된다.
  - 주문을 등록하면 주문 상태는 WAITING이다.
  - 하나 이상의 메뉴를 주문해야 한다.
  - 각 메뉴의 수량과 가격을 입력해야 한다.
  - 입력한 각 메뉴의 가격은 실제 메뉴판에 있는 가격과 같아야 한다.
  - 메뉴판에 노출된 메뉴만 주문할 수 있다.
  - 주문 타입이 배달이나 테이크아웃인 경우 주문한 메뉴의 수량이 0개 이상이어야 한다.
  - 주문 타입이 배달 일 경우, 배달 주소를 입력해야 한다.
  - 주문 타입이 매장 식사일 경우, 주문한 테이블을 입력해야 한다. 이때, 주문한 테이블은 사용 중인 상태여야 한다.

- 주문을 수락할 수 있다.
  - 주문 상태가 WAITING인 경우에만 주문을 수락할 수 있다.
  - 주문을 수락하면 주문 상태는 ACCEPTED이다.
  - 주문 타입이 배달인 경우 배달 기사에게 배달 요청을 보내야 한다.
  - 배달 요청 시, 주문 번호, 주문 총 가격, 배달 주소를 전송한다.

- 주문을 서빙할 수 있다.
  - 주문 상태가 ACCEPTED인 경우에만 주문을 서빙할 수 있다.
  - 주문을 서빙하면 주문 상태는 SERVED이다.
- 배달을 시작할 수 있다.
  - 주문 타입이 배달이고 주문 상태가 SERVED인 경우에만 배달을 시작할 수 있다.
  - 배달을 시작하면 주문 상태는 DELIVERING이다. 
- 배달 완료할 수 있다.
  - 주문 상태가 DELIVERING인 경우에만 배달은 완료할 수 있다.
  - 배달을 완료하면 주문 상태는 DELIEVERED이다.
- 주문을 완료할 수 있다.
  - 주문 타입이 배달인 경우 주문 상태가 DELIVERED인 경우에만 완료할 수 있다.
  - 주문 타입이 테이크아웃이나 매장 식사인 경우, 주문 상태가 SERVED인 경우에만 배달을 완료할 수 있다.
  - 주문을 완료하면 주문 상태는 COMPLETED이다.
- 전체 주문을 조회할 수 있다.
  
    
## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
