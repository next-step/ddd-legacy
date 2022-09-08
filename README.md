# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항

- 메뉴
  - 메뉴를 만든다.
    - 가격은 0 이상이어야 한다.
    - 메뉴는 분류가 지정되어야한다.
    - 메뉴의 상품 정보가 입력되어야한다.
    - 메뉴의 상품 양은 0 이상이어야 한다.
    - 메뉴의 상품은 이미 등록되어 있어야한다.
    - 메뉴의 가격은 메뉴 상품의 총합의 가격보다 작거나 같아야한다.
    - 메뉴의 이름 입력값은 비어있을 수 없고 비속어로 등록되어 있지 않아야한다.
  - 메뉴 가격을 수정한다.
    - 가격 입력값은 비어있을 수 없고 0이상이어야 한다.
    - 메뉴는 이미 등록된 상태이어야 한다.
    - 메뉴의 가격과 메뉴의 상품의 가격 총합이 같아야한다.
  - 메뉴를 노출한다.
    - 등록된 메뉴만 보여줄 수 있다.
    - 메뉴의 가격과 메뉴의 상품의 가격 총합이 같아야한다.
  - 메뉴를 숨긴다.
    - 등록된 메뉴만 숨길 수 있다.
  - 모든 메뉴를 보여준다.
- 메뉴 그룹
  - 메뉴그룹을 만든다.
    - 메뉴 그룹의 이름은 입력값이 존재하지 않거나 빈 값 수 없다.
  - 모든 메뉴 그룹을 보여준다.
- 주문
  ![주문 흐름도](https://user-images.githubusercontent.com/47442178/189153627-2fab8b2d-3e6b-4fc1-9713-d707cf93fd1d.png)
  - 주문을 생성한다.
    - 주문 타입은 입력값이 존재해야한다.
    - 주문 목록은 입력값이 존재하지 않거나 빈 값일 수 없다.
    - 실제 존재하는 메뉴와 주문 목록의 메뉴가 일치해야한다.
    - 매장 내 식사가 아니면 주문 목록의 수량이 0 이상이어야한다.
    - 주문 목록의 메뉴는 존재하는 메뉴이어야 한다.
    - 메뉴는 노출된 상태여야한다.
    - 메뉴의 실제 가격과 입력된 메뉴의 가격은 같아야한다.
    - 주문의 상태를 대기 상태로 설정한다.
    - 주문 타입이 배달이면 배달 주소가 필요하다.
      - 주문 배달 주소가 입력값이 없거나 빈 값일수 없다.
    - 주문 타입이 매장 내 식사라면 테이블 구분자가 필요하다.
      - 테이블은 등록된 테이블이어야한다.
      - 테이블에 앉아있는 상태여야한다.
  - 주문을 승인한다.
    - 주문은 존재하는 주문이어야한다.
    - 주문은 대기 상태이어야 한다.
    - 주문 타입이 배달이라면 주문 정보를 라이더에게 전달한다.
    - 주문 상태를 승인으로 변경한다.
  - 주문을 서빙한다.
    - 주문은 존재하는 주문이어야한다.
    - 주문의 상태는 승인 상태이어야 한다.
    - 주문 상태를 서빙완료 상태로 변경한다.
  - 주문 배달을 시작한다.
    - 주문은 존재하는 주문이어야한다.
    - 주문 타입은 배달이어야한다.
    - 주문 상태는 서빙 완료 상태이어야한다.
    - 주문 상태를 배달중으로 변경한다.
  - 주문 배달을 완료한다.
    - 주문은 존재하는 주문이어야한다.
    - 주문 상태는 배달중이어야한다.
    - 주문 상태를 배달완료로 변경한다.
  - 주문을 완료한다.
    - 주문은 존재하는 주문이어야한다.
    - 주문 타입이 배달이면
      - 상태는 배달 완료이어야한다.
    - 주문 타입이 테이크아웃 / 매장내 식사이면
      - 주문 상태는 배달완료 이어야한다.
    - 주문 상태를 완료로 변경한다.
    - 주문 타입이 매장내 식사라면
      - 주문 상태가 완료이고 주문 테이블이 있으면 자리를 비운다.
  - 모든 주문 상태를 보여준다.
- 주문 테이블
  - 주문 테이블을 생성한다.
    - 이름은 입력값이 존재하지 않거나 빈 값일 수 없다.
  - 주문 테이블에 앉는다.
    - 주문테이블은 등록된 상태이어야한다.
    - 테이블을 착석 상태로 변경한다.
  - 주문 테이블에 자리를 비운다.
    - 주문테이블은 등록된 상태이어야한다.
    - 주문 상태가 완료이고 주문 테이블이 있어야한다.
    - 주문 테이블을 비운다.
  - 주문 테이블 손님 수를 변경한다.
    - 손님 수가 0 이상이어야한다.
    - 주문테이블은 등록된 상태이어야한다.
    - 자리는 착석 상태이어야 한다.
  - 모든 주문 테이블을 보여준다.
- 상품
  - 상품을 생성한다.
    - 가격은 입력값은 비어있을 수 없고 0이상이어야 한다.
    - 이름은 입력값이 비어있을 수 없고 비속어로 등록되어있지 않아야 한다.
  - 상품 가격을 변경한다.
    - 가격은 입력값은 비어있을 수 없고 0이상이어야 한다.
    - 상품은 존재하는 상품이어야한다.
    - 상품 가격으로 인한 메뉴의 가격도 변경되어야한다.
    - 메뉴 가격과 상품구성의 가격 총합이 다르면 메뉴를 노출하지 않는다.
  - 모든 상품을 보여준다.



## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
