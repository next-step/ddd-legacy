# 키친포스

### step2 진행방법
* kitchenpos 패키지의 코드를 보고 키친포스의 요구 사항을 README.md에 작성한다.
* http 디렉터리의 .http 파일(HTTP client)을 보고 어떤 요청을 받는지 참고한다

## 요구 사항
* 가게에서 메뉴에 대한 주문을 처리할 수 있는 POS(Point Of Sales)를 구현한다.
* 상품
  * 새 상품을 등록할 수 있다.
  * 상품 이름은 비속어를 사용할 수 없다.
  * 이미 등록 된 상품의 가격을 변경할 수 있다.
  * 상품의 가격을 변경할 때 상품이 포함된 메뉴에 속한 메뉴상품들의 가격합을 재계산해서 메뉴의 가격이 가격합보다 크면 메뉴를 숨긴다.  
  * 상품 가격은 0 이상이어야 한다.
  * 상품 전체를 조회할 수 있다.
* 메뉴그룹
  * 새 메뉴그룹을 등록할 수 있다.
  * 메뉴그룹 이름은 빈 값일 수 없다.
  * 메뉴그룹 전체를 조회할 수 있다.
* 메뉴
  * 새 메뉴를 등록할 수 있다.
  * 메뉴의 가격은 0 이상이어야 한다.
  * 메뉴는 특정 메뉴그룹에 반드시 속해야 한다.
  * 메뉴는 1 개 이상의 상품을 포함하고 있어야 한다.
  * 메뉴에 포함되는 상품은 미리 등록된 상품이어야 한다.
  * 메뉴에 포함되는 상품의 수량은 0 이상이어야 한다.
  * 메뉴의 가격은 메뉴에 속한 메뉴상품들의 가격합보다 같거나 작아야한다.
  * 메뉴 이름은 비속어를 사용할 수 없다.
  * 이미 등록 된 메뉴의 가격을 변경할 수 있다.
  * 이미 등록 된 메뉴의 노출 여부를 변경할 수 있다.
  * 메뉴 전체를 조회할 수 있다.
* 주문테이블
  * 새 주문테이블을 등록할 수 있다.
  * 주문테이블 이름은 빈 값일 수 없다.
  * 이미 등록된 주문테이블에 손님이 사용중이라는 표시를 할 수 있다.
  * 이미 등록된 주문테이블에 빈 테이블이라는 표시를 할 수 있다.
  * 빈 테이블 표시는 완료되지 않은 주문이 있는 테이블에는 할 수 없다.
  * 이미 등록된 주문테이블에 테이블에 앉은 손님 수를 기록할 수 있다.
  * 테이블에 앉은 손님 수는 0 이상이어야 한다.
  * 전체 주문테이블을 조회할 수 있다.

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
