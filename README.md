# Step 2
- 포스(Pos) 애플리케이션을 구현한다.
- entity의 id는 임의로 생성된 문자열을 사용한다.
  - 문자열 규칙
    - 8자리-4자리-4자리-4자리-12자리의 문자열. 하이픈으로 연결된다.
    - 예시 3b528244-34f7-406b-bb7e-690912f66b10 
- [ ] 메뉴 그룹
  - [ ] 메뉴 그룹은 id와 이름을 가진다.
    - [ ] 메뉴 그룹명은 빈값일 수 없다.
  - [ ] 메뉴 그룹 전체를 조회한다.
  - [ ] 메뉴 그룹 추가한다.
- 메뉴
  - [ ] 메뉴 저장한다.
      - [ ] 메뉴 이름
        - [ ] 비속어를 사용할 수 없다.
      - [ ] 가격 
          - [ ] 수정할 수 있다.
          - [ ] 재고의 원가를 모두 합친 가격을 초과해야 한다.
      - [ ] 메뉴가 속한 메뉴 그룹
        - [ ] 항상 존재한다.
      - [ ] 노출여부 (pos 사용자에게 메뉴를 노출할지 여부)
        - [ ] on/off 할 수 있다.
      - [ ] 현재 재고
        - [ ] 재고의 id
        - [ ] 재고 수량
  - [ ] 메뉴 전체를 조회한다.
- 상품
  - [ ] 상품 이름과 가격을 저장한다.
  - [ ] 상품 가격을 수정한다.
  - [ ] 상품 목록을 조회한다.
- 테이블 주문
  - [ ] 주문 테이블을 추가 한다.
    - [ ] 테이블명, 손님수를 입력후, '미사용중'으로 상태값을 변경한다.
  - [ ] 좌석에 손님을 앉힌다.
    - '사용중'으로 상태값을 변경한다.
  - [ ] 테이블당 손님 수를 수정한다.
  - [ ] 주문 테이블을 초기화한다.
    - [ ] 주문의 상태는 '완료' 상태이어야 한다.
    - [ ] 손님 수 0명, 사용여부 '미사용'으로 초기화한다.
  - [ ] 주문 테이블 목록을 조회한다.
- 주문 
  - [ ] 주문 주소, 날짜, 주문 상태, 유형(테이블 주문, 포장, 배달), 주문 테이블 고유 id 로 이뤄진다.
  - [ ] 주문을 생성한다.
    - [ ] 여러개의 메뉴를 주문할 수 있다.
    - [ ] POS기에서 비노출된 상품은 주문할 수 없다.
    - [ ] 주문 총 금액은 메뉴를 합산한 금액과 일치한다.
    - [ ] 주문 초기 상태는 '대기'이다.
    - [ ] 배달 주문일 경우 주소를 주문 정보에 입력하고, 테이블 주문인 경우 테이블 id를 주문 정보에 입력한다.
  - [ ] 주문을 승낙한다.
    - [ ] 배달 주문이면 라이더를 호출한다.
      - [ ] 라이더는 주문 금액, 배송지 주소를 안다.
    - [ ] 주문 상태를 '주문수락'으로 변경한다.
  - [ ] 주문된 음식을 손님에게 서빙한다.
    - [ ] 테이블 주문, 포장만 가능하다.
    - [ ] 주문 상태를 '서빙완료'로 변경한다.
  - [ ] 주문된 음식을 손님에게 배송하기 시작한다.
    - [ ] 배달 음식만 가능하다.
    - [ ] 주문 상태를 '배달중'으로 변경한다.
  - [ ] 주문된 음식을 손님에게 배송 완료한다.
    - [ ] '배송중'인 음식만 가능하다.
    - [ ] '배달완료'로 상태 변경한다.
  - [ ] 주문 처리완료
    - [ ] 테이블주문, 포장의 경우 '서빙완료' 상태이어야만 한다.
    - [ ] 배달음식의 경우 '배달완료' 상태이어야만 한다.
    - [ ] 테이블주문일 경우, 해당 테이블의 손님 수를 0으로 리셋하고, 사용가능으로 상태값을 초기화한다.
  - [ ] 전체 주문을 조회한다. 

# Step 1
- [x] 쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환  
- [x] 앞의 기본 구분자(쉼표, 콜론) 외에 커스텀 구분자를 지정할 수 있다.  
커스텀 구분자는 문자열 앞부분의 “//”와 “\n” 사이에 위치하는 문자를 커스텀 구분자로 사용한다.  
예를 들어 “//;\n1;2;3”과 같이 값을 입력할 경우 커스텀 구분자는 세미콜론(;)이며, 결과 값은 6이 반환되어야 한다.
- [x] 문자열 계산기에 숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException 예외를 throw 한다.

# Step 0
- [x] 자동차 이름과 자동차 위치로 자동차를 생성한다.
- [x] 자동차 이름은 5글자를 넘을 경우 예외 발생한다.
- [x] 자동차는 움직임 값이 4 이상일때만 움직인다.
- [x] 자동차는 멈춘다.
- [x] 자동차는 0-9 사이의 숫자로 랜덤하게 움직인다.
