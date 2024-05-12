# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항

### STEP 0

- [x] racingcar 패키지의 Car에 대한 테스트 코드를 작성하며 JUnit 5에 대해 학습한다.
  - [x] 자동차 이름은 5 글자를 넘을 수 없다.
  - [x] 5 글자가 넘는 경우, IllegalArgumentException이 발생한다.
  - [x] 자동차가 움직이는 조건은 0에서 9 사이의 무작위 값을 구한 후, 무작위 값이 4 이상인 경우이다.
- [x] JUnit 5의 @DisplayName을 사용하여 테스트 메서드의 의도를 한글로 표현한다.
- [x] JUnit의 Assertions이 아닌 AssertJ의 Assertions를 사용한다.
- [x] 개발자는 코드를 작성하면서 확장성(scalability)에 대한 고민을 항상 해야 한다. 자동차가 다양한 방법으로 움직일 수 있게 구현한다.
- [x] 자동차가 조건에 따라 움직였는지 움직이지 않았는지 테스트하고자 할 때는 isBetween(), isGreaterThan(), isLessThan() 등은 사용하지 않는다.
- [x] 미션을 진행함에 있어 아래 문서를 적극 활용한다.
  - A Guide to JUnit 5
  - Guide to JUnit 5 Parameterized Tests

### STEP 1

- [x] 쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환 (예: “” => 0, "1,2" => 3, "1,2,3" => 6, “1,2:3” => 6)
- [x] 앞의 기본 구분자(쉼표, 콜론) 외에 커스텀 구분자를 지정할 수 있다. 커스텀 구분자는 문자열 앞부분의 “//”와 “\n” 사이에 위치하는 문자를 커스텀 구분자로 사용한다. 예를 들어 “//;\n1;2;3”과 같이 값을 입력할 경우 커스텀 구분자는 세미콜론(;)이며, 결과 값은 6이 반환되어야 한다.
- [x] 문자열 계산기에 숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException 예외를 throw 한다.


### STEP 2

- 메뉴
  - 메뉴를 만든다.
    - 가격은 0원 이상이어야 한다.
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
    - 메뉴 그룹의 이름은 입력값이 존재하지 않거나 빈 값일 수 없다.
  - 모든 메뉴 그룹을 보여준다.
- 주문
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
