# 키친포스

## 요구 사항

### 식당을 운영하는데 필요한 관리시스템을 개발한다.

- 메뉴그룹
  - 메뉴그룹 생성
    - null 또는 "" 을 생성될 메뉴그룹의 이름으로 지정할 수 없다.
  - 메뉴그룹 전체 조회

<br>

- 단품
  - 단품 생성
    - null 또는 음수값을 생성될 메뉴의 가격으로 정할 수 없다.
    - null 또는 비속어를 생성될 메뉴의 이름으로 정할 수 없다.
  - 단품 변경
    - null 또는 음수값으로 가격을 변경할 수 없다.
  - 단품 전체 조회

<br>

    - [ ] 새로운 메뉴의 이름을 입력해야 한다.
    - [ ] 새로운 메뉴의 가격을 입력해야하며 음수값을 입력할 수 없다.
    - [ ] 새로운 메뉴가 속할 메뉴 그룹을 입력해야 한다.
    - [ ] 새로운 메뉴를 구성할 단품의 구성을 입력해야 한다.
      - [ ] 새로운 메뉴를 구성하는 단품(들)은 모두 이미 등록되어 있는 단품들이어야 한다.
      - [ ] 새로운 메뉴를 구성하는 단품의 수량은 단품의 종류별로 적어도 1개 이상이어야 한다.
      - [ ] 새로운 메뉴의 가격은 새로운 메뉴를 구성하는 기존 단품의 가격의 합을 초과하면 안된다.
  - 메뉴 가격 변경 
    - [ ] 변경될 가격을 입력해야하며 음수값을 입력할 수 없다.
    - [ ] 변경될 가격은 해당 메뉴를 구성하는 기존 단품의 가격의 합을 초과하면 안된다.
  - 메뉴 숨김
  - 메뉴 목록 조회

<br>

<br>

- 주문
    - 주문 생성 
      - [ ] 주문의 종류로 배달(DELIVERY), 테이크아웃(TAKEOUT), 홀이용(EAT_IN) 중 하나를 반드시 선택해야한다. 
      - [ ] 주문에는 이미 등록된 메뉴가 포함되어 있어야 한다.
      - [ ] 주문의 종류가 배달 혹은 테이크아웃인 경우 주문을 구성하는 각 메뉴의 수량은 1개 이상이어야 한다.   
      - [ ] 주문에 포함된 메뉴는 제공 가능한 메뉴여야 한다.(노출되어 있는 메뉴여야 한다)
      - [ ] 주문의 종류가 배달인 경우 반드시 주소를 입력해야한다. 
      - [ ] 주문의 종류가 홀이용인 경우 이용할 테이블 식별값을 입력해야한다.
    - 주문 수락
      - [ ] 주문 수락 시점에 주문의 상태가 WAITING(대기) 상태여야 한다.
      - [ ] 주문이 수락되면 주문의 상태는 ACCEPTED(수락됨) 으로 변경된다.
    - 주문 서빙
      - [ ] 서빙 시점에 주문의 상태가 ACCEPTED(수락됨) 상태여야 한다.
      - [ ] 주문에 대한 서빙이 발생하면 해당 주문의 상태는 SERVED(서빙됨) 로 변경된다.
    - 주분 배달 시작
      - [ ] 주문의 종류가 DELIVERY(배달)이어야 한다.
      - [ ] 배달 시작 시점에 주문의 상태가 SERVED(서빙됨) 상태이면 안된다.
      - [ ] 주문에 대한 배달이 시작되면 주문의 상태가 DELIVERING(배달중) 으로 변경된다.
    - 주문 배달 완료
      - [ ] 주문의 종류가 DELIVERY(배달)이어야 한다.
      - [ ] 배달 완료 시점에 주문의 상태가 DELIVERING(배달중) 상태여야 한다.
      - [ ] 주문에 대한 배달이 완료되면 주문의 상태가 DELIVERED(배달됨) 으로 변경된다.
    - 주문 완료
      - [ ] 주문 종류가 DELIVERY(배달) 이면서, 주문이 아직 배달중인 주문(주문의 상태가 DELIVERED(배달됨)인 주문)은 완료 처리할 수 없다.
      - [ ] 주문의 종류가 TAKEOUT(테이크아웃) 또는 EAT_IN(홀이용) 인데 아직 서빙이 되지 않은(주문의 상태가 SERVED가 아닌) 경우 해당 주문을 완료 처리할 수 없다.
      - [ ] 주문의 종류가 EAT_IN(홀이용) 인 경우 이용한 테이블의 사용 인원을 0명 처리하고 테이블의 상태를 빈 테이블로 변경한다.
      - [ ] 주문 완료 시점에 주문의 상태가 COMPLETED(완료됨) 로 변경된다.
    - 모든 주문 조회

<br>

- 테이블
    - 테이블 생성 
      - [ ] 생성할 테이블의 이름을 입력해야 한다.
      - [ ] 테이블 생성시점의 해당 테이블의 이용객 수는 0 명으로 처리해야 한다.
      - [ ] 테이블 생성시점의 해당 테이블의 이용 여부는 '비어있음' 상태로 처리해야 한다.
    - 테이블 착석
      - [ ] 테이블 착석 처리 시점에 테이블의 이용 여부가 '비어있지 않음' 상태로 처리되어야 한다.
    - 테이블 정리
      - [ ] 정리처리를 하는 시점에 해당 테이블에서 발생된 주문의 상태가 COMPLETED(완료됨) 이어야 한다.
      - [ ] 테이블 정리 시점에 해당 테이블의 이용객 수는 0 명으로 변경되어야 한다.
      - [ ] 테이블 정리 시점에 해당 테이블의 이용 여부는 '비어있음' 상태로 변경 되어야 한다.
    - 테이블 인원 수를 변경
    - 모든 테이블 조회

<br>

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
