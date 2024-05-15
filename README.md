# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항
- kitchenpos 라는 서비스를 구현한다.
- 상품
    - [ ] 상품 등록이 가능하다. 
      (상품이름: 후라이드 / 가격: 16000)
      - [ ] 상품 이름을 꼭 입력해야 한다.
      - [ ] 상품 이름에는 비속어가 들어갈 수 없다.
      - [ ] 상품 가격을 꼭 입력해야 한다.
      - [ ] 가격은 음수일수 없다.
    - [ ] 등록된 상품의 가격을 수정할 수 있다.
      - [ ] 가격은 음수일 수 없다.
      - [ ] 해당 상품으로 구성된 메뉴의 가격이 수정된 상품의 가격 총합보다 크면 메뉴를 비노출한다.
    - [ ] 등록된 상품 목록을 조회할 수 있다. 
- 메뉴 그룹
    - [x] 메뉴 그룹 등록이 가능하다. 
      (이름 : 한마리메뉴)
      - [x] 메뉴 그룹 이름을 꼭 입력해야 한다.
    - [x] 등록된 메뉴 그룹 목록을 조회할 수 있다.
- 메뉴
    - [ ] 메뉴를 등록할 수 있다.
      - [ ] 메뉴는 메뉴 그룹에 속한다.
      - [ ] 메뉴는 이름, 가격, 노출여부, 메뉴 구성 상품 정보를 꼭 입력해야 한다. 
        (이름: 후라이드치킨 / 가격: 16000, 노출여부: 노출 / 메뉴 구성: 상품-후라이드)
        - [ ] 메뉴 이름에는 비속어가 들어갈 수 없다.
        - [ ] 메뉴의 가격은 `0원` 보다 작을 수 없다.
        - [ ] 메뉴의 가격은 `메뉴 구성 상품` 가격의 총합보다 클 수 없다.
        - [ ] 메뉴의 `메뉴 구성 상품` 은 이미 등록된 `상품` 으로만 등록 가능하다.
        - [ ] `메뉴 구성 상품` 의 수량은 `0개` 보다 작을수 없다.
    - [ ] 메뉴는 가격 수정이 가능하다.
      - [ ] 메뉴의 가격은 `0원` 보다 작을 수 없다.
      - [ ] 메뉴의 가격은 `메뉴 구성 상품` 가격의 총합보다 클 수 없다.
    - [ ] 메뉴는 노출상태로 변경 가능하다.
      - [ ] 메뉴의 가격이 `메뉴 구성 상품` 가격 총합보다 클 수 없다.
    - [ ] 메뉴는 비노출상태로 변경 가능하다.
    - [ ] 등록된 메뉴 목록을 조회할 수 있다.
- 주문
    - [ ] 주문은 `주문상태 (대기, 접수, 서빙완료, 배송중, 배송완료, 완료)` 가 존재하며 상황에 따라 주문상태는 변경된다.
      - [ ] 최초 주문시 `대기` 상태로 등록된다.
    - [ ] 주문자는 주문시 메뉴, 수량, 주문유형 
      (매장식사, 배송, 테이크아웃) 정보를 입력해야 한다.
    - [ ] 메뉴가 `노출` 상태이어야 주문 가능하다.
    - [ ] 주문한 메뉴의 가격과 실제 메뉴의 가격이 같아야 한다.
    - [ ] `매장식사` 유형으로 주문이 가능하다.
      - [ ] 주문전에 테이블에 앉아서 주문해야 한다.
      - [ ] 메뉴, 수량 정보를 입력해야 한다.
        (메뉴: 후라이드 치킨 / 수량: 1 / 주문유형: 매장식사 / 주문상태: 대기)
      - [ ] 메뉴가 접수되면 `접수` 상태로 변경된다.
        (메뉴: 후라이드 치킨 / 수량: 1 / 주문유형: 매장식사 / 주문상태: 접수)
      - [ ] 메뉴가 준비되면 `서빙완료` 상태로 변경된다.
        (메뉴: 후라이드 치킨 / 수량: 1 / 주문유형: 매장식사 / 주문상태: 서빙완료)
      - [ ] 손님이 메뉴를 가져가면 사장님은 주문을 `완료` 상태로 변경하고 테이블을 정리할 수 있다.
        (메뉴: 후라이드 치킨 / 수량: 1 / 주문유형: 매장식사 / 주문상태: 완료)
    - [ ] `배송` 유형으로 주문이 가능하다.
      - [ ] 메뉴, 수량, 배송지 정보를 입력해야 한다. 
        (메뉴: 후라이드 치킨 / 수량: 1 / 배송지: nextstep 사무실 / 주문유형: 배송 / 주문상태: 대기)
      - [ ] 주문 수량이 `0개` 일수 없다.
      - [ ] `라이더 요청` 이 성공되면 `접수` 상태로 변경된다.
        (메뉴: 후라이드 치킨 / 수량: 1 / 배송지: nextstep 사무실 / 주문유형: 배송 / 주문상태: 접수)
      - [ ] 서빙이 완료되면 `서빙완료` 상태로 변경된다.
        (메뉴: 후라이드 치킨 / 수량: 1 / 배송지: nextstep 사무실 / 주문유형: 배송 / 주문상태: 서빙완료)
      - [ ] 배송이 시작되면 `배송중` 상태로 변경된다.
        (메뉴: 후라이드 치킨 / 수량: 1 / 배송지: nextstep 사무실 / 주문유형: 배송 / 주문상태: 배송중)
      - [ ] 배송이 완료되면 `배송완료` 상태로 변경된다.
        (메뉴: 후라이드 치킨 / 수량: 1 / 배송지: nextstep 사무실 / 주문유형: 배송 / 주문상태: 배송완료)
      - [ ] 사장님은 `배송완료` 상태를 확인하면 `완료` 상태로 변경 가능하다.
        (메뉴: 후라이드 치킨 / 수량: 1 / 배송지: nextstep 사무실 / 주문유형: 배송 / 주문상태: 완료)
    - [ ] `테이크아웃` 유형으로 주문이 가능하다.
      - [ ] 메뉴, 수량 정보를 입력해야 한다.  
        (메뉴: 후라이드 치킨 / 수량: 1 / 주문유형: 매장식사 / 주문상태: 대기)
      - [ ] 주문 수량이 `0개` 일수 없다.
      - [ ] 메뉴가 접수되면 `접수` 상태로 변경된다. 
        (메뉴: 후라이드 치킨 / 수량: 1 / 주문유형: 매장식사 / 주문상태: 접수)
      - [ ] 메뉴가 준비되면 `서빙완료` 상태로 변경된다. 
        (메뉴: 후라이드 치킨 / 수량: 1 / 주문유형: 매장식사 / 주문상태: 서빙완료)
      - [ ] 손님이 메뉴를 가져가면 사장님은 주문을 `완료` 상태로 변경 가능하다. 
        (메뉴: 후라이드 치킨 / 수량: 1 / 주문유형: 매장식사 / 주문상태: 완료)
    - [ ] 생성된 주문 목록을 조회할 수 있다.
- 테이블
    - [ ] 사장님은 가게의 테이블에 이름을 부여하여 등록할 수 있다.
    - [ ] 최초 테이블의 상태는 다음과 같이 등록된다.
      (손님 0명 / 빈자리)
    - [ ] 손님이 테이블에 앉으면 테이블의 상태는 다음과 같이 변경된다.
      (1번 테이블 / 손님 4명 / 사용중)
    - [ ] 테이블을 사용하는 손님의 숫자를 변경 가능하다.
      (1번 테이블 / 손님 3명 / 사용중)
    - [ ] 손님이 테이블을 사용 완료하여 테이블을 정리하면 다시 상태를 변경할 수 있다.
      (1번 테이블 '손님 0명' / '빈자리')
    - [ ] 주문 상태가 `완료` 인 경우에만, 해당 주문 테이블을 정리할 수 있다.

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
