# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항

- 식당 포스기를 구현한다
### - products 제품
- [X] 식별자, 이름, 가격으로 제품을 생성할 수 있다.
  - [X] 가격은 반드시 있어야 한다
  - [X] 가격은 0 보다 작을 수 없다
  - [X] 이름은 반드시 있어야 한다
  - [X] 이름에 욕설이 들어있으면 안된다.
    - 별도의 외부서비스를 이용해서 확인한다
  - 식별자는 UUID 를 이용한다
    - [X] UUID는 생성해서 직접 넣어준다
- [X] 가격을 변경할 수 있다.
  - [X] 가격은 반드시 있어야 한다
  - [X] 가격은 0 보다 작을 수 없다
  - [X] 가격변경된 제품을 포함하는 메뉴의 가격이 메뉴에 포함된 제품들의 가격 * 수량의 합계보다 크다면 메뉴를 숨긴다
- [X] 모든 제품을 조회할 수 있다
### - orders 주문
- [ ] 주문을 생성할 수 있다
  - [ ] 주문은 식별자를 가지며 UUID 를 이용한다
    - [ ] 식별자는 직접 생성해서 넣어준다
  - [ ] 주문은 주문 타입과 주문 상태를 갖는다.
    - 주문상태는 waiting(대기), accepted(수락), served(서빙), delivering(배달시작), delivered(배달완료), completed(주문완료) 케이스가 있다
    - 주문 타입에는 Delivery(배달주문), Takeout(포장), EatIn(매장식사)가 있다
    - 초기 주문상태는 waiting 이다.
  - [ ] 전달받은 주문타입은 반드시 존재한다
    - [ ] 주문타입이 배달이라면 배달주소를 반드시 전달받는다.
    - [ ] 주문타입이 매장식사라면 주문매장테이블을 조회해서 주문데이터에 같이 저장한다. 존재하지 않으면 주문을 생성하지 않는다.
  - [ ] 1개이상의 메뉴, 수량 정보를 전달받는다
    - [ ] 주문에 해당하는 메뉴들을 조회했을 때, 메뉴, 수량정보와 개수가 다르다면 주문을 생성하지 않는다
    - [ ] 주문이 매장식사타입이 아니라면 메뉴에 해당하는 수량은 0이상이어야 한다
    - [ ] 하나의 메뉴라도 숨겨져있다면 주문은 생성하지 않는다
    - [ ] 메뉴의 가격과 전달받은 가격정보가 다르다면 주문을 생성하지 않는다
- [ ] 주문을 수락(accept)할 수 있다.
  - [ ] 주문식별자를 전달받아 주문을 조회하여 주문이 없거나 대기상태가 아니라면 수락하지 않는다.
  - [ ] 배달주문의 경우엔 주문된 메뉴들의 (가격과 수량의 곱)의 합, 주문식별자, 배달주소를 외부서비스에 전달하여 배달요청을 한다.
- [ ] 주문을 서빙(serve)할 수 있다.
  - [ ] 주문식별자를 전달받아 주문을 조회하여 주문이 없거나 수락상태가 아니라면 서빙하지 않는다.
- [ ] 주문 배달시작(start-delivery) 할 수 있다
  - [ ] 주문 식별자를 전달받아 주문을 조회하여 주문이 없거나 배달주문이 아니라면 배달 시작하지 않는다.
  - [ ] 서빙상태가 아니라면 배달을 시작하지 않는다.
- [ ] 주문 배달완료(complete-delivery) 할 수 있다
  - [ ] 주문 식별자를 전달받아 주문을 조회하여 주문이 없거나 배달상태가 아니라면 배달을 완료하지 않는다.
- [ ] 주문 완료(complete)할 수 있다
  - [ ] 주문 식별자를 전달받아 주문이 없으면 완료하지 않는다.
  - [ ] 배달주문인데 배달완료상태가 아니라면 주문완료 상태로 변경하지 않는다.
  - [ ] 포장이나 매장식사의 경우 서빙상태가 아니라면 주문완료 상태로 변경하지 않는다.
  - [ ] 매장식사의 경우 주문정보를 조회했을 때 주문완료상태라면 매장주문테이블의 손님수는 0, 미사용중으로 변경한다.
- [ ] 모든 주문을 조회할 수 있다
### - menus 메뉴
- [ ] 메뉴를 생성할 수 있다
  - [ ] 메뉴Id, 가격, 이름, 메뉴그룹Id, 메뉴제품, 초기 전시유무를 전달받는다.
  - [ ] 메뉴Id는 UUID 를 식별자로 사용한다.
    - [ ] UUID는 생성해서 직접 넣어준다
  - [ ] 가격은 반드시 존재한다
  - [ ] 가격은 0 보다 작을 수 없다
  - [ ] 이름은 반드시 있어야 한다
  - [ ] 이름에 욕설이 들어있으면 안된다.
    - [ ] 별도의 외부서비스를 이용해서 확인한다
  - [ ] 전달받은 메뉴그룹 ID 로 메뉴그룹을 조회한 후 메뉴에 세팅한다.
  - [ ] 만약 메뉴그룹이 없다면 메뉴를 저장하지 않는다
  - [ ] 메뉴제품들을 전달받아서 값이 없거나 비어있다면 메뉴를 저장하지 않는다.
  - [ ] 메뉴제품을 이용하여 메뉴에 해당하는 모든 제품을 조회한다.
    - [ ] 만약 메뉴에 해당하는 제품들의 개수가 전달받은 메뉴제품 개수와 다르다면 메뉴를 저장하지 않는다.
    - [ ] 제품들을 조회한 후 해당 제품의 가격과 전달받은 메뉴제품의 수량을 곱해서 새로운 메뉴제품을 만든다.
      - [ ] 메뉴제품의 수량은 0보다 작을 수 없다
      - [ ] 전달받은 가격이 해당 계산 값보다 크다면 메뉴는 저장하지 않는다.
- [ ] 메뉴의 가격을 변경할 수 있다
  - [ ] 전달받은 메뉴식별자에 해당하는 메뉴가 없다면 실행되지 않는다.
  - [ ] 가격은 반드시 존재한다
  - [ ] 가격은 0 보다 작을 수 없다
- [ ] 메뉴를 전시상태로 바꿀 수 있다
  - [ ] 메뉴의 가격이 메뉴에 포함된 제품들의 가격 * 수량의 합보다 크다면 실행되지 않는다.
- [ ] 메뉴를 숨길 수 있다.
- [ ] 모든 메뉴를 조회할 수 있다.
### - menuGroups 메뉴그룹
- [X] 이름을 전달받아 메뉴그룹을 생성할 수 있다
  - [X] 이름은 반드시 존재해야하며 길이는 1이상이다
  - 식별자는 UUID 를 사용하며 직접 생성해서 넣어준다
- [X] 모든 메뉴그룹을 조회할 수 있다
### - orderTables 주문매장테이블
- [ ] 주문매장테이블을 생성할 수 있다
  - [ ] 아이디, 이름이 필요하다.
    - [ ] 이름은 반드시 존재하며 길이가 0보다 크다
    - [ ] 아이디는 UUID 를 직접 넣어준다
  - [ ] 초기 손님수는 0이고, 미사용 상태이다.
- [ ] 주문매장테이블을 사용중으로 변경할 수 있다.(sit)
- [ ] 주문매장테이블을 정리할 수 있다(clear)
  - [ ] 해당 테이블에서 주문이 주문완료(Completed) 가 아니라면 정리할 수 없다.
  - [ ] 정리하고 나면 미사용상태로 바뀌고 손님수는 0이다
- [ ] 손님의 수를 변경할 수 있다.
  - [ ] 변경할 손님의 수는 0 이상이어야 한다
  - [ ] 사용중인 테이블만 손님의 수를 변경한다.
- [ ] 모든 주문매장테이블을 조회할 수 있다.
## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
