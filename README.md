# 키친포스

## 요구사항

식당 관리를 위한 포스기를 구현한다.

* 메뉴 그룹
  * [ ] 여러 메뉴들을 모아서 이름을 붙여 그룹(group)으로 추가한다. `POST /api/menu-groups`
    * [ ] 메뉴 그룹의 이름은 반드시 지정되어야 한다.
  * [ ] 모든 메뉴 그룹을 조회한다. `GET /api/menu-groups`
* 메뉴
  * [ ] 여러 상품을 가지고 메뉴를 추가한다. `POST /api/menus`
    * [ ] 메뉴 추가 시, 메뉴 그룹과 display 여부를 지정한다.
    * [ ] 가격은 반드시 지정되어야 한다.
      * [ ] 가격은 0 이상 이어야 한다.
    * [ ] 하나의 존재하는 메뉴 그룹에 속해야 한다.
    * [ ] 하나 이상의 상품이 포함되어 있어야 한다.
      * [ ] 상품들은 모두 존재하는 상품이어야 한다.
      * [ ] 메뉴 상품의 상품수량은 모두 0 이상이어야 한다.
      * [ ] 메뉴 가격은 메뉴에 포함된 상품들의 가격과 갯수를 곱한 총액보다 작거나 같아야 한다.
    * [ ] 메뉴 이름은 반드시 지정되어야 한다.
      * [ ] 메뉴 이름은 비속어가 포함될 수 없다.
    * [ ] 메뉴는 보여지거나, 숨겨져서 보여지지 않을 수 있다.
      * [ ] display 여부를 지정하지 않으면 기본적으로 보여지지 않는다.
  * [ ] 메뉴 가격을 수정한다. `PUT /api/menus/{menuId}/price`
    * [ ] 존재하는 메뉴여야 한다.
    * [ ] 가격은 반드시 지정되어야 한다.
      * [ ] 가격은 0 이상 이어야 한다.
    * [ ] 메뉴 가격은 메뉴에 포함된 상품들의 가격과 갯수를 곱한 총액보다 작거나 같아야 한다.
  * [ ] 메뉴 노출 여부를 수정한다. (display / hide)
    * [ ] 메뉴 노출 시, 존재하는 메뉴여야 하고, 가격 조건을 만족해야 한다. `PUT /api/menus/{munuId}/display`
    * [ ] 메뉴 숨김 시, 존재하는 메뉴여야 한다. `PUT /api/menus/{menuId}/hide`
  * [ ] 모든 메뉴를 조회한다. `GET /api/menus`

* 상품
  * [ ] 상품을 새롭게 추가한다. `POST /api/products`
    * [ ] 가격은 반드시 지정되어야 한다.
      * [ ] 가격은 0 이상 이어야 한다.
    * [ ] 상품명은 반드시 지정되어야 한다.
      * [ ] 상품명에 비속어가 포함될 수 없다.
  * [ ] 가격을 수정한다. `PUT /api/products/{productId}/price`
    * [ ] 존재하는 상품의 정보만 수정할 수 있다.
    * [ ] 가격은 반드시 지정되어야 한다.
      * [ ] 가격은 0 이상 이어야 한다.
    * [ ] 해당 상품이 들어가 있는 메뉴들를 확인해서 상품의 총합 값보다 비싸진 메뉴가 있다면 메뉴를 숨김 처리한다.
  * [ ] 모든 상품 조회한다. `GET /api/products`

* 주문 테이블
  * [ ] 주문 테이블을 추가한다. `POST /api/order-tables`
    * [ ] 주문 테이블 이름은 반드시 지정되어야 한다.
    * [ ] 새로 추가되는 주문 테이블은 비어있으며, 손님은 0명이다.
  * [ ] 주문 테이블에 손님이 앉는다. (sit) `PUT /api/order-tables/{orderTableId}/sit`
    * [ ] 존재하는 테이블이어야 한다.
    * [ ] 테이블이 비어있는 상태가 아님을 표시한다.
  * [ ] 주문 테이블을 정리한다. (clear) `PUT /api/order-tables/{orderTableId}/clear`
    * [ ] 존재하는 테이블이어야 한다.
    * [ ] 주문 처리가 완료된 상태여야 한다.
    * [ ] 테이블이 비어있는 상태임을 표시한다.
    * [ ] 손님이 0명인 상태임을 표시한다.
  * [ ] 테이블에 앉은 손님의 수를 변경한다. `PUT /api/order-tables/{orderTableId}/number-of-guests`
    * [ ] 손님의 인원은 0명 이상이어야 한다.
    * [ ] 존재하는 테이블이어야 한다.
    * [ ] 비어있지 않은 테이블이어야 한다.
  * [ ] 모든 주문 테이블을 조회한다. `GET /api/order-tables`

* 주문

  * [ ] 주문 타입은 `DELIVERY`, `TAKEOUT`,` EAT_IN` 가 있다.
  * [ ] 주문 상태는 `WAITING`, `ACCEPTED`, `SERVED`, `DELIVERING`, `DELIVERED`, `COMPLETED` 가 있다.

  * [ ] 주문한다. `POST /api/orders`
    * [ ] 주문 타입이 지정되어있어야 한다.
    * [ ] 주문 상품은 필수이다.
    * [ ] 주문 상품의 메뉴는 모두 존재하는 메뉴여야 한다.
    * [ ] 주문 타입이 배달, 포장인 경우 주문 상품 수량은 0 이상이어야 한다.
    * [ ] 주문 상품의 메뉴는 모두 전시(display)된 상태여야 한다.
    * [ ] 메뉴의 가격과 주문 상품의 가격은 같아야 한다.
    * [ ] 주문 직후는 주문이 대기(`WAITING`) 상태이다.
    * [ ] 주문 타입이 배달인 경우,
      * [ ] 배달 주소는 필수이다.
    * [ ] 주문 타입이 매장 내 식사인 경우,
      * [ ] 주문 테이블이 존재해야 한다.
      * [ ] 주문 테이블이  비어있으면 안된다.
  * [ ] 주문을 승인 한다. `PUT /api/orders/{orderId}/accept`
    * [ ] 존재하는 주문이어야 한다.
    * [ ] 대기 상태인 주문이어야 한다.
    * [ ] 주문 타입이 배달인 경우, 주문 정보(주문 가격, 배달지 주소)를 전달한다. 
    * [ ] 주문 상태를 승인으로 변경한다.
  * [ ] 주문을 서빙한다. `PUT /api/orders/{orderId}/serve`
    * [ ] 존재하는 주문이어야 한다.
    * [ ] 주문은 승인된 상태여야 한다.
    * [ ] 주문 상태를 서빙 완료로 변경한다.
  * [ ] 배달을 시작한다. `PUT /api/orders/{orderId}/start-delivery`
    * [ ] 존재하는 주문이어야 한다.
    * [ ] 주문 타입이 배달이어야 한다.
    * [ ] 주문이 준비 완료된 상태여야 한다.
    * [ ] 주문 상태를 배달 중으로 변경한다.
  * [ ] 배달을 완료한다. `PUT /api/orders/{orderId}/complete-delivery`
    * [ ] 존재하는 주문이어야 한다.
    * [ ] 주문 타입이 배달이어야 한다.
    * [ ] 주문이 배달 중 상태여야 한다.
    * [ ] 주문 상태를 배달 완료로 변경한다.
  * [ ] 주문을 완료한다. `PUT /api/orders/{orderId}/complete`
    * [ ] 존재하는 주문이어야 한다.
    * [ ] 주문 타입이 배달인 경우, 배달 완료된 상태여야 한다.
    * [ ] 주문 타입이 포장, 매장 내 식사인 경우, 주문이 서빙 완료 상태여야 한다.
    * [ ] 주문을 처리 완료 상태로 변경한다.
    * [ ] 매장 내 식사인 경우, 주문 테이블을 정리한다.
  * [ ] 주문 목록을 조회한다. `GET /api/orders`

## 용어 사전

| 한글명         | 영문명           | 설명                                                         |
| -------------- | ---------------- | ------------------------------------------------------------ |
| 주문 테이블    | Order Table      | 손님이 앉는 테이블                                           |
| 주문           | Order            | 주문                                                         |
| 메뉴           | Menu             | 주문 가능한 메뉴                                             |
| 메뉴 그룹      | Menu Group       | 여러 메뉴들을 그룹지어서 담는다.                             |
| 상품           | Product          | 메뉴가 제공하는 상품. 한 상품이 여러 개의 메뉴에 속할 수 있다. |
| 비속어         | Profanity        | 이름 설정 시 필터링해야 하는 단어                            |
| 메뉴 상품      | Menu Product     | 메뉴에 속한 상품                                             |
| 손님 인원      | Number Of Guests | 한 테이블에 앉아있는 손님의 인원                             |
| 주문 방식      | Order Type       | 주문 방식은 배달, 포장, 매장 내 식사가 있다.                 |
| 배달           | DELIVERY         | 주문 타입                                                    |
| 포장           | TAKEOUT          | 주문 타입                                                    |
| 매장 내 식사   | EAT_IN           | 주문 타입                                                    |
| 주문 상품      | Order Line Item  | 주문되어진 메뉴                                              |
| 주문 상태      | Order Status     | 대기, 승인, 서빙완료, 배송중, 배송완료, (주문처리)완료 상태가 있다. |
| 대기           | Waiting          | 손님이 주문한 직후의 상태                                    |
| 승인           | Accepted         | 대기주문을 매장에서 승인한 상태                              |
| 서빙 완료      | Served           | 포장 혹은 매장 내 식사인 경우 상품을 손님에게 서빙 완료,<br />배달인 경우 상품 준비 완료 |
| 배달 중        | Delivering       | 배달을 출발 상태                                             |
| 배달 완료      | Delivered        | 배달이 완료된 상태                                           |
| 주문 처리 완료 | Completed        | 주문 처리가 완료된 상태                                      |
| 배달기사       | Kitchen rider    | 배달 기사                                                    |

## 모델링

![kitchenpos_03](https://user-images.githubusercontent.com/35985636/127991426-ffafdfb7-bcbe-4054-812f-d37d8729a140.png)
