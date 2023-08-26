# 키친포스

## step2 요구사항
- [x] kitchenpos 패키지의 코드를 보고 키친포스의 요구 사항을 README.md에 작성한다.
    - [x] 상품에 대한 요구사항 정리
    - [x] 메뉴에 대한 요구사항을 정리
    - [x] 메뉴 그룹에 대한 요구사항을 정리 
    - [x] 주문 테이블 요구사항 정리
    - [x] 주문에 대한 요구사항 정리

## step3 요구사항
- 통합 테스트 코드 작성
  - [ ] 상품(Product Business Object) 에 대한 테스트 작성
  - [ ] 메뉴(Menu Business Object) 에 대한 테스트 작성
  - [ ] 메뉴 그룹(Menu Group Business Object) 에 대한 테스트 작성
  - [ ] 주문 테이블(Order Table Business Object) 에 대한 테스트 작성
  - [ ] 주문(Order Business Object) 에 대한 테스트 작성
- Controller 테스트 코드 작성
  - [ ] ProductRestController 테스트 작성
  - [ ] MenuRestController 테스트 작성
  - [ ] MenuGroupRestController 테스트 작성
  - [ ] OrderTableRestController 테스트 작성
  - [ ] OrderRestController 테스트 작성


## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항
- 식당 포스기 애플리케이션을 구현한다.
- **상품 (Product)**
   - 판매하는 상품을 의미한다.
   - [x] 새로운 상품을 등록할 수 있다.
     - [x] 상품은 ( 상품고유ID, 상품 이름, 가격 ) 을 가진다.
     - [x] 상품을 등록할 때 이름, 가격은 필수 값이다.
     - [x] 상품의 가격은 0이상의 양수로만 등록가능하다.
       - [x] 상품의 가격이 비어있거나 음수라면 사용자에게 등록 불가능함을 알려야한다.
     - [x] 상품의 이름은 불건전한 언어로 등록할 수 없다.(욕설, 음란한 언어, 등등..)
   - [x] 기존 상품의 가격을 수정할 수 있다.
     - [x] 상품의 가격을 수정할 때도 가격은 0이상의 양수로만 수정가능하다.
       - [x] 상품의 가격을 수정할 때 가격 값이 0미만의 음수라면 사용자에게 등록 불가능함을 알려야한다.
     - [x] 존재하지 않는 상품에 대한 가격 변경은 불가능하다.
     - [x] 상품 가격 변경 시 (변경하려는 상품의 가격 > 해당 상품이 포함된 메뉴의 가격)라면 해당 메뉴는 전시되지 않도록 변경한다.
   - [x] 전체 상품의 정보를 조회할 수 있다.
     - [x] 각 상품의 ( 상품고유ID, 상품 이름, 가격 ) 정보를 알 수 있어야한다.

- **메뉴 (Menu)**
  - 고객에게 상품을 보여주는 것을 메뉴라고 부른다.
  - [ ] 메뉴는 (메뉴고유ID, 메뉴 이름, 메뉴가격, 메뉴가 속한 그룹 정보, 메뉴 전시 여부, 메뉴 상품(메뉴에 포함된 상품)) 을 가진다.
  - [ ] 새로운 메뉴를 등록할 수 있다.
    - [x] 메뉴는 하나 이상의 상품으로 구성될 수 있다.
      - [x] 메뉴에 상품이 없는것은 허용하지 않는다.
      - [x] 메뉴의 한 종류의 상품에 대한 개수를 가질 수 있다. ex) 두마리 후라이드 치킨메뉴 - (후라이드치킨 상품 2개로 구성)
        - [x] 메뉴의 한 종류의 상품에 대한 개수는 0보다 작을 수 없다.
    - [x] 메뉴는 가격을 가진다.
      - [x] 메뉴의 가격은 0이상의 양수로만 등록가능하다.
        - [x] 메뉴의 가격이 비어있거나 음수라면 사용자에게 등록 불가능함을 알려야한다.
      - [x] 메뉴의 가격은 가지고있는 각각의 상품 가격을 더한 가격보다 작거나 같아야한다.
        - 메뉴의 가격 <= (A상품의 가격 * A상품의 개수) + (B상품의 가격 * B상품의 개수) ... + (N상품의 가격 * N상품의 개수) 
    - [x] 하나의 메뉴는 하나의 메뉴 그룹에 속해야한다.
      - [x] 메뉴가 속할 그룹은 존재하는 메뉴 그룹이어야 한다.
    - [x] 메뉴를 등록할 때 전시 여부를 결정할 수 있다
      - [x] 메뉴를 전시 OR 숨김 설정이 가능해야한다.
    - [x] 메뉴는 고객 또는 사용자에게 전시할 이름을 가진다.
      - [x] 메뉴의 이름은 비어있거나 불건전한 용어를 가질 수 없다.(욕설, 음란한 언어, 등등..)
    - [x] 메뉴를 구성하는 상품은 존재하는 상품만 가능하다.
      - [x] 등록하는 메뉴 정보에 존재하지 않는 상품이 있다면 사용자에게 불가능함을 알려야한다.
  - [x] 메뉴는 가격을 변경할 수 있다.
    - [x] 메뉴 가격 변경 시 가격 값은 필수값이다.
    - [x] 메뉴 가격 변경 시 가격 값은 0이상의 양수여야한다.
      - [x] 메뉴 가격 변경 시 가격 값이 0미만의 음수라면 사용자에게 메뉴를 등록할 수 없음을 알려야한다.
    - [x] 가격 변경하려는 메뉴는 존재하는 메뉴여야한다.
    - [x] 가격 변경하려는 가격은 메뉴에 포함된 상품들의 총 가격보다 작거나 같아야한다.
      - 메뉴의 가격 <= (A상품의 가격 * A상품의 개수) + (B상품의 가격 * B상품의 개수) ... + (N상품의 가격 * N상품의 개수)
      - 메뉴의 가격이 메뉴에 포함된 상품들의 총 가격보다 크다면 사용자에게 가격 변경이 불가능함을 알려야한다.
  - [x] 메뉴를 전시 상태로 변경할 수 있다.
    - [x] 전시 상태를 변경하려는 메뉴는 존재하는 메뉴여야 한다. 
    - [x] 메뉴를 사용자에게 전시하려고 할 때 (메뉴의 가격 > 가지고 있는 상품의 총 가격) 이라면, 메뉴는 노출시킬 수 없다.
  - [x] 메뉴를 숨김 상태로 변경할 수 있다.
    -  [x] 숨김 상태를 변경하려는 메뉴는 존재하는 메뉴여야 한다.
  - [x] 메뉴의 전체 정보를 조회할 수 있어야 한다.
    - [x] 각 메뉴의 (메뉴고유ID, 메뉴 이름, 메뉴가격, 메뉴가 속한 그룹 정보, 메뉴 전시 여부, 메뉴 상품(메뉴에 포함된 상품)) 정보를 알 수 있어야한다.
  

- **메뉴 그룹 (Menu Group)**
  - [x] 메뉴의 그룹을 지정해줄 수 있다. ex) 치킨집이라면 메뉴판에 추천메뉴, 신메뉴, 한마리 메뉴, 두마리 메뉴, 순살 메뉴 로 그룹을 지어줄 수 있다.
  - [x] 메뉴 그룹은 이름을 가진다.
    - [x] 메뉴 그룹의 이름은 1글자 이상이어야한다.
  - [x] 메뉴 그룹을 등록할 수 있다.
    - [x] 메뉴 그룹의 이름이 비어있다면 등록할 수 없다.
  - [x] 전체 메뉴 그룹을 조회할 수 있다.
    - [x] 각 메뉴 그룹의 (메뉴 그룹 고유번호, 메뉴 그룹 이름) 정보를 알 수 있어야한다.

- **주문 (Order)**
  - 하나의 고객이 구입한 메뉴들을 하나의 주문이라고 한다.
  - [ ] 주문은 (주문 고유번호, 주문 유형, 주문 상태, 주문 일시, 주문 메뉴들(주문에 담긴 메뉴), 배달 주소, 주문 테이블) 정보를 가질 수 있다.
  - [ ] 주문 유형은 다음과 같다.
    - 배달 - DELIVERY
    - 포장 - TAKE_OUT
    - 매장 식사 - EAT_IN
  - [ ] 주문의 유형은 필수 값으로 배달, 테이크아웃, 매장식사 중 하나를 선택해야한다.
  - [ ] 주문 상태는 다음과 같다.
    - 대기 - WAITING
    - 수락 - ACCEPTED
    - 제공 - SERVED 
    - 배달 - DELIVERING
    - 배달완료 - DELIVERED
    - 주문 완료 - COMPLETED
  - [ ] 주문 메뉴는 하나 이상의 정보가 존재해야 한다.
    - [ ] 주문에 등록할 메뉴는 존재하는 메뉴여야 한다.
    - [ ] 주문에 등록된 메뉴는 고객에게 전시중인 메뉴여야 한다.
      - [ ] 전시중인 메뉴가 아니면 주문이 불가능하다.
    - [ ] 주문유형이 매장식사(EAT_IN)가 아닐 때 주문 메뉴의 메뉴 개수는 0 미만의 음수가 될 수 없다. 
      - ex) 1번 주문은 배달(또는 포장) 주문이고, 후라이드 치킨 메뉴를 1개 주문했다. -> 가능
      - ex) 1번 주문은 배달(또는 포장) 주문이고, 후라이드 치킨 메뉴를 0개 주문했다. -> 불가능
      - ex) 1번 주문은 매장 주문이고, 후라이드 치킨 메뉴를 0개 주문했다. -> 가능
      - 왜 존재하는 제약사항인지 파악을 못함
      - 매장 식사일 때는 0미만의 개수를 가진 메뉴를 등록할 수 있다는 것이 이해가 안됨
    - [ ] 주문 메뉴는 실제 메뉴와 가격이 동일해야 한다.
  - [ ] 새로운 주문을 등록할 수 있다.
    - [ ] 주문을 새로 등록하면 주문 상태는 대기(WATING) 상태이다.
    - [ ] 배달 주문(DELIVERY)일 때 주문 테이블 정보는 가질 필요가 없다.
    - [ ] 배달 주문은 배달 주소가 필수 값이다.
    - [ ] 매장 식사(EAT_IN) 주문은 주문 테이블 정보가 필수이다.
      - [ ] 매장 식사 주문은 사용중인 주문 테이블만 주문 등록이 가능하다.
  - [ ] 주문의 상태를 수락(ACCEPT)상태로 변경할 수 있다.
    - [ ] 변경하고자 하는 주문은 존재하는 주문이어야한다.
    - [ ] 주문 상태가 대기(WAITING)이 아니라면 수락 상태로 변경할 수 없다.
    - [ ] 배달 주문이라면 (주문고유 번호, 주문의 가격, 배달 주소) 정보를 배달기사 배정 회사에 보내서 배달을 요청해야한다.
  - [ ] 주문을 제공(SERVED) 상태로 변경할 수 있다.
    - [ ] 변경하고자 하는 주문은 존재하는 주문이어야한다.
    - [ ] 주문 상태가 수락(ACCEPT)상태가 아니라면 제공 상태로 변경할 수 없다.
  - [ ] 주문을 배달 중(DELIVERING) 상태로 변경할 수 있다.
    - [ ] 배달 주문일 때만 변경이 가능하다.
      - [ ] 포장(TAKE_OUT), 매장 식사(EAT_IN)라면 배달 중 상태로 변경이 불가능하다.
      - [ ] 주문이 제공상태가 아니라면 변경이 불가능하다.
  - [ ] 배달 완료(DELIVERED) 상태로 변경이 가능하다.
    - [ ] 주문 상태가 배달중 상태가 아니라면 변경이 불가능하다.
  - [ ] 주문 완료(COMPLETE) 상태로 변경이 가능하다.
    - [ ] 배달 주문일 때 배달 완료(DELIVERED)가 아니라면 주문 완료로 변경이 불가능하다.
    - [ ] 포장 또는 매장 식사 일 때 제공(SERVED)상태가 아니라면 주문 완료로 변경이 불가능하다.
    - [ ] 주문 완료 변경 후 매장 식사 주문이라면 주문 테이블을 비어있는 테이블로 변경해야한다.
  - [ ] 전체 주문정보를 조회할 수 있다.
    - [ ] 각 주문의 (주문 고유번호, 주문 유형, 주문 상태, 주문 일시, 주문 메뉴들(주문에 담긴 메뉴), 배달 주소, 주문 테이블) 정보를 받을 수 있다.
  
- **주문 테이블 (Order Table)**
  - 고객이 매장에 앉는 테이블을 주문 테이블이라고 한다.
  - [ ] 주문 테이블은 (주문테이블 고유번호, 이름, 테이블에 앉은 고객 수, 사용 여부)를 가진다.
  - [x] 새로운 주문 테이블을 등록할 수 있다.
    - [x] 주문 테이블의 이름은 필수값이며 1글자 이상으로 등록해야한다.
      - [x] 주문 테이블의 이름이 비어있다면 사용자에게 잘못된 등록임을 알려야한다.
    - [x] 새롭게 등록하는 주문 테이블의 `테이블에 앉은 고객 수`는 `0명`으로 생성된다. 
    - [x] 새롭게 등록하는 주문 테이블의 `사용 여부`는 `사용하지 않는 상태`로 생성된다.
  - [ ] 주문 테이블에 고객이 앉았음을 등록할 수 있다.
  - [ ] 고객이 앉았던 주문 테이블 정보를 비어있는 테이블로 변경할 수 있다.
    - [ ] 주문 테이블의 주문 상태가 `주문 완료(COMPLETED)`가 아니라면 비어있는 테이블로 변경할 수 없다.
    - [ ] 비어있는 테이블이란 (고객수 = 0, 테이블 미사용) 상태를 의미한다.
  - [ ] 주문 테이블에 앉은 고객 수를 변경할 수 있다.
    - [ ] 수정할 때 고객 수는 0이상의 양수여야 한다.
      - [ ] 고객 수를 0미만의 음수로 등록하면 사용자에게 잘못된 등록임을 알려야한다.
    - [ ] 주문 테이블이 사용 중인 상태일 때만 고객 수를 변경할 수 있다.
  - [ ] 전체 주문 테이블의 정보를 조회할 수 있다.
    - [ ] 각 주문 테이블의 (주문테이블 고유번호, 이름, 테이블에 앉은 고객 수, 사용 여부)를 조회할 수 있어야한다.

## 용어 사전
[menu-groups.http](http%2Fmenu-groups.http)
| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
| 상품 | Product | 판매하는 상품을 의미한다. |
| 상품 - 고유ID | id | 판매하는 상품의 고유 번호를 의미한다. |
| 상품 - 이름 | name | 판매하는 상품의 이름을 의미한다. |
| 상품 - 가격 | price | 판매하는 상품의 가격을 의미한다. |
| 메뉴 | Menu | 고객에게 노출되는 메뉴 항목을 의미한다. |
| 메뉴 - 이름 | name | 메뉴의 이름을 의미한다. |
| 메뉴 - 가격 | price | 메뉴의 가격을 의미한다. |
| 메뉴 - 전시 여부 | displayed | 메뉴를 노출할지 안할지의 여부를 의미한다. (노출함, 숨김의 상태를 가짐) |
| 메뉴 - 전시 상태 | display | 메뉴를 노출하는 상태를 의미 |
| 메뉴 - 숨김 상태 | hide | 메뉴를 숨기는 상태를 의미 |
| 메뉴 상품 | Menu Product | 메뉴와 연결된 상품을 의미한다. 메뉴 상품 1개는 1개의 메뉴와 1개의 상품으로 구성된다. (1개의 메뉴는 여러개의 메뉴 상품을 가질 수 있음) |
| 메뉴 상품 - 개수 | quantity | 메뉴 상품에 있는 상품의 개수를 의미한다. ex) (두마리 치킨 - 후라이드 치킨 2개) 로 표현될 수 있음 |
| 메뉴 - 이름 | name | 메뉴의 이름을 의미한다. |
| 메뉴 그룹 | Menu Group | 메뉴의 그룹(카테고리)를 의미한다. ex) 치킨집이라면 메뉴판에 추천메뉴, 신메뉴, 한마리 메뉴, 두마리 메뉴, 순살 메뉴 로 그룹을 지어줄 수 있다. |
| 주문 테이블 | Order Table | 매장의 테이블을 의미한다. |
| 주문 테이블 - 이름 | name | 주문 테이블의 이름을 의미한다. ex) 1번, A1 등등 |
| 주문 테이블 - 사용 여부 | occupied | 주문 테이블의 사용 여부를 의미한다. (사용함, 사용하지 않음의 상태를 가짐) |
| 주문 테이블 - 고객 수 | number of guests | 주문 테이블에 앉아있는 고객 수를 의미한다. |
| 주문 | Order | 고객이 구입한 메뉴들을 하나의 주문으로 표현한다. |
| 주문 메뉴 | Order Line Item | 주문에 포함된 메뉴 중 하나를 의미한다. 하나의 주문은 여러개의 메뉴를 포함할 수 있다. |
| 주문 유형 | Order Type | 주문의 유형을 의미한다. |
| 주문 유형 - 배달 | Delivery | 배달 주문임을 의미하는 유형이다. |
| 주문 유형 - 매장 식사 | Eat In | 매장 식사 주문임을 의미하는 유형이다. |
| 주문 유형 - 포장 | Take Out | 포장 주문임을 의미하는 유형이다. |
| 주문 상태 | Order Status | 주문의 상태를 의미한다. |
| 주문 상태 - 대기 | Wating | 주문이 막 들어왔을 때 주방에서 조리 전인 상태를 의미한다. |
| 주문 상태 - 수락 | Accept | 주문을 수락하여 주방에서 조리 중인 상태를 의미한다. |
| 주문 상태 - 제공 | SERVED | 주문에 있는 메뉴들을 조리완료하여 제공이 가능한 상태를 의미한다.매장식사, 포장 주문일 때는 고객에게 전달이 가능한 상태를 의미. 배달 주문일 때는 배달기사에게 전달 가능한 상태를 의미. |
| 주문 상태 - 배달 중 | DELIVERING | 배달 주문을 배달 중인 상태를 의미한다. |
| 주문 상태 - 배달 완료 | DELIVERED | 배달 주문을 완료한 상태를 의미한다. |
| 주문 상태 - 주문 완료 | COMPLETED | 주문이 완료된 상태를 의미한다. |
| 주문 - 배달 주소 | Delivery Address | 배달 주문의 도착지 주소를 의미한다. |
| 주문 - 주문 일시 | Order Date Time | 주문이 들어온 일시를 의미한다. |

## 모델링
