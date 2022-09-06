# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항
* 간단한 음식점 포스기 시스템을 구현한다
* 상품
  * 상품등록
    * [ ] 상품은 이름과 가격을 입력하여 등록할 수 있다.
    * [ ] 상품 가격은 0원 보다는 작을 수 없다.
    * [ ] 상품 이름은 비어있을 수 없다.
    * [ ] 상품 이름에는 비속어를 포함할 수 없다.
    * [ ] 상품이 정상적으로 등록되면 상품 정보를 응답한다.
  * 상품가격변경
    * [ ] 상품 가격은 상품 ID와 변경할 상품가격 정보를 입력하여 변경할 수 있다. 
    * [ ] 변경할 상품 가격은 0원 보다 작을 수 없다.
    * [ ] 상품이 존재하지 않으면 상품 가격을 변경할 수 없다.
    * [ ] 상품이 등록된 메뉴의 가격보다 해당 메뉴의 등록된 메뉴상품금액(메뉴상품가격 * 수량)의 합이 더 높아지면 메뉴를 내린다.
  * 상품조회
    * [ ] 등록된 상품 목록을 조회할 수 있다.
* 메뉴그룹
  * 메뉴그룹 등록
    * [ ] 메뉴그룹은 이름을 입력하여 등록할 수 있다.
    * [ ] 메뉴그룹 이름은 비어있을 수 없다.
    * [ ] 메뉴그룹이 정상적으로 등록되면 메뉴그룹 정보를 응답한다.
  * 메뉴그룹 조회
    * [ ] 등록된 메뉴그룹 목록을 조회할 수 있다.
* 메뉴
  * 메뉴등록
    * [ ] 메뉴는 이름, 가격, 메뉴그룹, 진열상태, 메뉴상품들을 입력하여 등록할 수 있다.
    * [ ] 메뉴상품은 상품ID, 수량을 가지고 있다.
    * [ ] 메뉴에는 메뉴상품 여러개가 포함될 수 있다.
    * [ ] 메뉴 가격은 0원 보다 작을 수 없다.
    * [ ] 메뉴 이름은 비어있을 수 없다.
    * [ ] 메뉴 이름에는 비속어를 포함할 수 없다.
    * [ ] 메뉴그룹이 존재하지 않으면 메뉴를 등록할 수 없다.
    * [ ] 요청 메뉴상품이 없거나 비어있을 수 없다.
    * [ ] 메뉴상품이 존재하지 않으면 메뉴를 등록할 수 없다.
    * [ ] 메뉴상품 수량이 0개 보다 작을 수 없다.
    * [ ] 메뉴가격은 메뉴의 포함된 메뉴상품금액(메뉴상품가격 * 수량)의 총액 보다 더 높을 수 없다.
    * [ ] 상품이 정상적으로 등록되면 상품 정보를 응답한다.
  * 메뉴가격변경
    * [ ] 메뉴 가격은 메뉴ID와 변경할 메뉴가격 정보를 입력하여 변경할 수 있다.
    * [ ] 메뉴 가격은 0원 보다 작을 수 없다.
    * [ ] 메뉴가 존재하지 않으면 메뉴 가격을 변경할 수 없다.
    * [ ] 메뉴가격은 메뉴의 포함된 메뉴상품금액(메뉴상품가격 * 수량)의 총액 보다 더 높을 수 없다.
  * 메뉴진열
    * [ ] 메뉴는 메뉴ID를 통해 메뉴를 진열할 수 있다.
    * [ ] 메뉴가 존재하지 않으면 메뉴를 진열할 수 없다.
    * [ ] 메뉴가격은 메뉴의 포함된 메뉴상품금액(메뉴상품가격 * 수량)의 총액 보다 더 높을 수 없다.
  * 메뉴숨기기
    * [ ] 메뉴는 메뉴ID를 통해 메뉴를 숨길 수 있다.
    * [ ] 메뉴가 존재하지 않으면 숨길 수 없다.
  * 메뉴 조회
    * [ ] 등록된 메뉴 목록을 조회할 수 있다.
* 주문
  * 주문생성
    * [ ] 주문은 주문타입, 주문테이블ID, 주문상품들을 입력받아 생성할 수 있다.
    * [ ] 주문타입은 배달, 테이크아웃, 매장식사 중 선택할 수 있다.
    * [ ] 주문상품은 메뉴ID, 주문메뉴가격, 주문수량을 가지고 있다.
    * [ ] 주문타입이 올바르지 않으면 주문을 생성할 수 없다.
    * [ ] 주문상품들은 비어있을 수 없다.
    * [ ] 주문상품 메뉴가 존재하지 않으면 주문을 생성할 수 없다.
    * [ ] 배달 주문인 경우 주문상품수량이 0개 보다 작을 수 없다.
    * [ ] 테이크아웃 주문인 경우 주문상품수량이 0개 보다 작을 수 없다.
    * [ ] 주문상품 메뉴가 진열 중이 아니면 주문을 생성할 수 없다.
    * [ ] 주문상품 가격과 메뉴가격이 일치하지 않으면 주문을 생성할 수 없다.
    * [ ] 배달 주문인 경우 배달주소가 비어있을 수 없다.
    * [ ] 매장식사 주문인 경우 주문테이블이 존재하지 않으면 주문을 생성할 수 없다.
    * [ ] 주문이 정상적으로 생성되면 주문 정보를 응답한다.
  * 주문 접수
    * [ ] 주문 ID를 입력받아 주문을 접수할 수 있다.
    * [ ] 주문이 대기 중일 경우에는 접수할 수 없다.
    * [ ] 주문타입이 배달인 경우 라이더스에게 주문ID, 메뉴가격, 배송지 정보를 통해 배달을 요청한다.
    * [ ] 주문이 정상적으로 접수되면 주문 정보를 응답한다.
  * 주문 메뉴 제공
    * [ ] 주문 ID를 입력받아 제공이 완료된 주문건을 제공완료 상태로 변경할 수 있다.
    * [ ] 주문이 접수상태일 경우만 제공완료 상태로 변경 가능하다
    * [ ] 주문이 정상적으로 제공완료 상태로 변경되면 주문 정보를 응답한다.
  * 주문 배송 시작
    * [ ] 주문 ID를 입력받아 주문건을 배송 중 상태로 변경한다.
    * [ ] 주문타입이 배달인 경우에만 진행할 수 있다.
    * [ ] 주문이 제공완료 상태인 경우에는 배송 중 상태로 변경할 수 없다. 
    * [ ] 주문이 정상적으로 배송 중 상태로 변경되면 주문 정보를 응답한다.
  * 주문 배송 완료
    * [ ] 주문 ID를 입력받아 주문건을 배송완료 상태로 변경한다.
    * [ ] 주문이 배송 중인 경우에만 배송완료 상태로 변경 가능하다.
    * [ ] 주문이 정상적으로 배송완료 상태로 변경되면 주문 정보를 응답한다.
  * 주문 완료
    * [ ] 주문 ID를 입력받아 주문완료 상태로 변경할 수 있다.
    * [ ] 주문 타입이 배달인 경우 배송완료 상태가 아니면 주문완료 상태로 변경할 수 없다.
    * [ ] 주문 타입이 테이크아웃 이거나 매장식사인 경우 제공완료 상태가 아니면 주문완료 상태로 변경할 수 없다.
    * [ ] 주문 타입이 매장식사인 경우 주문테이블에 포함된 주문 건이 모두 완료된 상태라면 주문테이블을 정리한다.(미사용 처리 및 손님 수를 초기화)
  * 주문 조회
    * [ ] 생성된 주문 목록을 조회할 수 있다.
* 주문테이블
  * 주문테이블 생성
    * [ ] 주문테이블은 이름, 손님 수, 사용 중 여부를 가지고 있다.
    * [ ] 주문테이블은 테이블명을 입력받아 생성할 수 있다.
    * [ ] 주문테이블 이름은 비어있을 수 없다.
    * [ ] 주문테이블 생성시 손님 수 0명으로 사용 중 여부는 미사용으로 처리한다.
    * [ ] 주문테이블 정상적으로 생성되면 주문테이블 정보를 응답한다.
  * 주문테이블 손님 입석
    * [ ] 주문테이블 ID를 입력받아 주문테이블 사용 중 처리할 수 있다.
    * [ ] 주문테이블이 존재하지 않을 경우 처리할 수 없다.
    * [ ] 정상적으로 사용 중 처리가 완료되면 주문테이블 정보를 응답한다.
  * 주문테이블 정리
    * [ ] 주문테이블 ID를 입력받아 주문테이블을 정리할 수 있다
    * [ ] 주문테이블이 존재하지 않을 경우 처리할 수 없다.
    * [ ] 주문테이블에 포함된 주문 건 중 주문완료 처리가 안된 주문건이 존재할 경우 처리할 수 없다.
    * [ ] 주문테이블 손님 수 0명으로 사용 중 여부는 미사용으로 초기화한다.
  * 주문테이블 손님수 변경
    * [ ] 주문테이블 ID와 변경 손님 수 입력받아 손님 수를 변경할 수 있다.
    * [ ] 주문테이블 변경 손님 수는 0원 보다 작을 수 없다.
    * [ ] 주문테이블이 존재하지 않을 경우 처리할 수 없다.
    * [ ] 주문테이블이 사용 중 상태가 아니면 처리할 수 없다.
  * 주문테이블 조회
    * [ ] 생성된 주문테이블 목록을 조회할 수 있다.

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링



