# 키친포스

## 퀵 스타트

```sh
cd docker
sudo docker compose -p kitchenpos up -d
```

## 요구 사항
- 손님들에게 주문을 받아 메뉴를 판매할 수 있는 애플리케이션을 개발하는 것이 목표입니다.
- 상품
    - [ ] 상품을 등록할 수 있어야 합니다.
        - [ ] 단가는 필수 항목이며, 음수의 값을 갖지 않아야 합니다.
        - [ ] 상품의 이름은 필수 항목이며, 비속어를 포함하지 않아야 합니다.
    - [ ] 상품의 단가를 변경할 수 있어야 합니다.
        - [ ] 단가는 필수 항목이며, 음수의 값을 갖지 않아야 합니다.
        - [ ] 메뉴에 등록 된 단가보다 낮게 변경하면, 전시 된 메뉴를 숨길 수 있어야 합니다.
    - [ ] 등록된 상품을 조회할 수 있어야 합니다.
- 메뉴 그룹
    - [ ] 판매 될 상품이 그룹으로 묶여 전시되는데, 이를 '메뉴 그룹' 이라고 합니다.
    - [ ] 메뉴 그룹을 등록할 수 있어야 합니다.
        - [ ] 메뉴 그룹의 이름은 필수 항목입니다.
    - [ ] 등록된 메뉴 그룹을 조회할 수 있어야 합니다.
- 메뉴
    - [ ] 상품은 '메뉴'를 통해 고객에게 판매 할 수 있습니다.
    - [ ] 메뉴를 등록할 수 있어야 합니다.
        - [ ] 메뉴 가격은 필수 항목이며, 음수의 값을 갖지 않아야 합니다.
        - [ ] 메뉴 그룹은 필수 항목입니다.
        - [ ] 1개 이상의 상품을 포함해야 합니다.
        - [ ] 메뉴에 등록될 상품의 수량은 음수의 값을 갖지 않아야 합니다.
        - [ ] 메뉴 가격은 상품의 단가와 수량을 곱한 금액의 합산보다 높게 설정할 수 없습니다.
        - [ ] 메뉴 이름은 필수 항목이며, 비속어를 포함하지 않아야 합니다.
    - [ ] 메뉴의 판매 가격을 변경할 수 있어야 합니다.
        - 메뉴 가격은 필수 항목이며, 음수의 값을 갖지 않아야 합니다.
        - 메뉴 가격은 상품의 단가와 수량을 곱한 금액의 합산보다 높게 설정할 수 없습니다.
    - [ ] 메뉴를 전시할 수 있어야 합니다.
        - 메뉴 가격은 필수 항목이며, 음수의 값을 갖지 않아야 합니다.
    - [ ] 메뉴를 숨길 수 있어야 합니다.
    - [ ] 등록된 메뉴를 조회할 수 있어야 합니다.
- 주문
    - [ ] 메뉴를 주문할 수 있어야 합니다.
        - [ ] 메뉴는 특정 장소로 배달하거나 직접 방문해서 가져 가거나 매장 내에서 먹을 수 있습니다.
        - [ ] 최소 1개 이상의 메뉴를 주문해야 합니다.
        - [ ] 매장 내에서 취식을 하는 경우가 아니라면 메뉴의 수량이 0개 이상 있어야 합니다.
        - [ ] 전시 된 메뉴만 주문 가능합니다.
        - [ ] 배달 주문인 경우 장소 정보를 받아야 합니다.
        - [ ] 매장 내 식사일 경우 테이블을 이용 할 수 있는지 확인해야 합니다.
    - [ ] 주문한 메뉴의 접수 상태를 확인 할 수 있어야 합니다.
        - [ ] 배달 주문인 경우 배달 라이더를 부릅니다.
    - [ ] 주문한 메뉴의 전달 상태를 확인 할 수 있어야 합니다.
    - [ ] 배달 주문인 경우 배달 시작 상태를 확인 할 수 있어야 합니다.
    - [ ] 배달 종료시 종료 상태를 확인 할 수 있어야 합니다.
    - [ ] 메뉴가 전달되거나 식사가 완료되면 주문 완료 상태를 확인 할 수 있어야 합니다.
    - [ ] 주문 정보 및 진행 상태를 조회할 수 있어야 합니다.
- 테이블
    - [ ] 매장 내 식사를 위해 주문 전 테이블을 미리 등록해야 합니다.
        - [ ] 테이블 이름은 필수 항목입니다.
    - [ ] 테이블이 사용 중인지 확인 할 수 있어야 합니다.
    - [ ] 테이블의 상태를 초기화 할 수 있어야 합니다.
    - [ ] 테이블에 앉아 있는 손님의 수를 변경 할 수 있어야 합니다.
    - [ ] 테이블 정보를 조회할 수 있어야 합니다.

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
