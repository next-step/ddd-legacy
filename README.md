# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항
* 간단한 음식점 포스기 시스템을 구현한다
* 메뉴
  * [ ] 메뉴는 이름과 가격을 가지고 있다.
  * [ ] 메뉴는 진열 상태를 관리할 수 있다.
  * [ ] 메뉴는 하나의 메뉴그룹에 속한다.
  * [ ] 메뉴는 하나 이상의 상품을 가진다.
* 메뉴그룹
  * [ ] 메뉴 그룹은 이름을 가지고 있다.
  * [ ] 메뉴 그룹은 하나 이상의 메뉴를 가진다.
* 상품
  * [ ] 상품은 이름과 가격을 가지고 있다.
  * [ ] 상품은 하나 이상의 메뉴의 속한다.
* 주문
  * [ ] 주문은 3가지의 주문 타입(배달, 테이크아웃, 매장식사)을 선택할 수 있다.  
  * [ ] 주문은 대기, 접수, 제공, 배달 중, 배달완료, 주문완료 총 6가지 상태를 가진다.
  * [ ] 주문은 주문시간을 가지고 있다.
  * [ ] 매장식사 주문에 경우 하나의 주문 테이블에 속한다.
  * [ ] 배달 주문에 경우 배달 주소지를 입력받는다.
  * [ ] 주문은 하나 이상의 주문항목을 가진다.
  * [ ] 주문항목은 하나의 메뉴와 수량을 가진다.
* 주문테이블
  * [ ] 주문테이블은 이름과 손님 수를 알 수 있다.
  * [ ] 주문테이블은 사용 중인지 아닌지 알 수 있다.
  * [ ] 주문테이블은 하나 이상의 주문을 가진다.

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링



