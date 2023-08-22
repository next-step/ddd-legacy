# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항

- 가게에서 사용할 포스 시스템을 구현한다.
- 상품 (Product)
  - [ ] 새로운 상품을 등록한다.
    - 상품에 대한 가격은 0원 이상이어야 한다.
    - 상품명은 비어있을 수 없고, 255자를 초과할 수 없다.
    - 상품명에 비속어가 포함되어 있으면 안 된다.
  - [ ] 기존 상품의 가격을 변경한다.
    - 상품에 대한 가격은 0원 이상이어야 한다.
  - [ ] 모든 상품을 가져온다.
- 메뉴 그룹 (Menu Group)
  - [ ] 새로운 메뉴 그룹을 등록한다.
    - 메뉴 그룹명은 비어있을 수 없고, 255자를 초과할 수 없다.
  - [ ] 모든 메뉴 그룹을 가져온다.
- 메뉴
  - [ ] 새로운 메뉴를 등록한다.
  - [ ] 기존 메뉴의 가격을 변경한다.
  - [ ] 등록한 메뉴의 상품들을 노출한다.
  - [ ] 노출한 메뉴를 숨긴다.
  - [ ] 모든 메뉴를 가져온다.
- 주문
  - [ ] 새로운 주문을 등록한다.
  - [ ] 들어온 주문을 승낙한다.
  - [ ] 승낙한 주문을 제공한다.
  - [ ] 주문에 대한 배달을 시작한다.
  - [ ] 주문에 대한 배달을 완료한다.
  - [ ] 주문을 완료한다.
  - [ ] 모든 주문을 가져온다.
- 주문 테이블
  - [ ] 새로운 테이블을 등록한다.
  - [ ] 등록된 테이블에 고객을 입장시킨다.
  - [ ] 등록된 테이블에 고객을 퇴장시킨다.
  - [ ] 입장된 테이블의 고객 수를 변경한다.
  - [ ] 모든 테이블을 가져온다.

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
