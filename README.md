# 키친포스

## 요구 사항
- 키친 포스 기능을 구현한다.
- 메뉴 그룹 (Menu-group)
    - [ ]  사용자는 메뉴 그룹을 등록할 수 있고, 등록이 완료되면 등록된 메뉴 그룹 정보를 반환받아 확인할 수 있다.
    - [ ]  메뉴 그룹의 이름이 null 인 경우 등록을 실패한다.
    - [ ]  사용자는 등록된 모든 메뉴 그룹의 목록을 조회할 수 있다.
- 음식 (Product)
    - [ ]  사용자는 음식을 등록할 수 있고, 등록이 완료되면 등록된 음식 정보를 반환받아 확인할 수 있다.
    - [ ]  음식의 가격이 null 이거나 0 보다 작을 경우 IllegalArgumentException 을 throw 한다.
    - [ ]  사용자는 등록된 모든 음식의 목록을 조회할 수 있다.
- 메뉴 (Menu)
    - [ ]  사용자는 메뉴를 등록할 수 있고, 등록이 완료되면 등록된 메뉴 정보를 반환받아 확인할 수 있다.
    - [ ]  등록하려는 메뉴의 가격이 null 이거나 0 보다 작을 경우 IllegalArgumentException 을 throw 한다.
    - [ ]  등록하려는 메뉴가 속할 메뉴그룹이 존재하지 않는다면 IllegalArgumentException 을 throw 한다.
    - [ ]  등록하려는 메뉴를 구성하는 음식(Product)들이 하나라도 존재하지 않는다면 IllegalArgumentException 을 throw 한다.
    - [ ]  메뉴의 가격이 메뉴를 구성하는 음식들의 가격을 모두 합한 것보다 크다면 IllegalArgumentException 을 throw 한다.
    - [ ]  사용자는 등록된 모든 메뉴의 목록을 조회할 수 있다.
- 테이블 (Table)
    - [ ]  사용자는 새 테이블을 등록할 수 있고, 등록이 완료되면 등록된 테이블 정보를 반환받아 확인할 수 있다.
    - [ ]  사용자는 등록된 테이블의 정보 (테이블 그룹, 착석 여부, 착석 손님 수)를 변경할 수 있다.
    - [ ]  사용자는 테이블의 착석 여부만 변경할 수 있다.
    - [ ]  존재하지 않는 테이블의 착석여부를 변경하려 할 경우 IllegalArgumentException 을 throw 한다.
    - [ ]  테이블 그룹에 속하지 않은 테이블의 착석여부를 변경하려 할 경우 IllegalArgumentException 을 throw 한다.
    - [ ]  착석여부를 변경하려는 테이블의 주문상태가 '요리중' 이거나 '식사중'인 경우 IllegalArgumentException 을 throw 한다.
    - [ ]  사용자는 테이블의 착석 손님 수를 변경할 수 있다.
    - [ ]  변경하려는 착석 손님 수가 0보다 작을 경우 IllegalArgumentException 을 throw 한다.
    - [ ]  존재하지 않는 테이블의 착석 손님 수를 변경하려 할 경우 IllegalArgumentException 을 throw 한다.
    - [ ]  비어있는 상태의 테이블의 착석 손님 수를 변경하려 할 경우 IllegalArgumentException 을 throw 한다. 
    - [ ]  사용자는 등록된 테이블의 목록을 조회할 수 있다.
- 테이블 그룹 (Table-group)
    - [ ]  사용자는 테이블 그룹을 등록할 수 있고, 등록이 완료되면 등록된 테이블 그룹 정보를 반환받아 확인할 수 있다.
    - [ ]  등록된 테이블 그룹에 속한 테이블들은 테이블 그룹의 ID를 가지고 있고, 비어있지 않아야 한다. 
    - [ ]  등록하려는 테이블 그룹에 속한 테이블 목록이 null 이거나 2개 미만인 경우 IllegalArgumentException 을 throw 한다.
    - [ ]  등록하려는 테이블 그룹에 속한 테이블 목록 중 하나라도 존재하지 않는다면 IllegalArgumentException 을 throw 한다.
    - [ ]  등록하려는 테이블 그룹에 속한 테이블 목록 중 하나라도 비어있지 않거나 다른 테이블 그룹에 이미 속해 있다면 IllegalArgumentException 을 throw 한다.
    - [ ]  사용자는 테이블 그룹을 삭제할 수 있다.
    - [ ]  삭제하려는 테이블 그룹에 속한 테이블들의 주문상태가 '요리중' 이거나 '식사중'인 경우 IllegalArgumentException 을 throw 한다.
    - [ ]  삭제된 테이블 그룹에 속해 있던 테이블은 테이블 그룹의 ID가 null 이고, 비어있어야 한다.
- 주문 (Order)
    - [ ]  사용자가 메뉴가 지정되지 않은 주문을 등록하려 하면 IllegalArgumentException 을 throw 한다.
    - [ ]  등록하려는 주문에 존재하지 않는 메뉴가 하나라도 있다면 IllegalArgumentException 을 throw 한다.
    - [ ]  등록하려는 주문의 테이블 정보가 존재하지 않는다면 IllegalArgumentException 을 throw 한다.
    - [ ]  등록하려는 주문의 테이블이 착석 상태가 아니라면 IllegalArgumentException 을 throw 한다.
    - [ ]  사용자는 주문 정보를 등록할 수 있다.
    - [ ]  사용자는 등록된 주문 목록을 조회할 수 있다.
    - [ ]  등록되지 않은 주문의 상태를 변경하려 하면 IllegalArgumentException 을 throw 한다.
    - [ ]  서빙이 완료된 주문의 상태를 변경하려 하면 IllegalArgumentException 을 throw 한다.
    - [ ]  사용자는 주문의 상태를 변경할 수 있다.
## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
