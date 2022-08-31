# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항
- 메뉴그룹
  - [ ] 여러개의 메뉴들을 하나의 메뉴그룹으로 관리할 수 있다. ( 메뉴그룹 : 메뉴 = 1 : N 관계 )
  - [ ] 메뉴 그룹을 등록할 수 있다.
    - [ ] 메뉴 그룹 ID는 고유한 UUID를 생성해서 사용한다.
    - [ ] 메뉴그룹명은 필수이며 최대 255자까지 입력할 수 있다.
  - [ ] 메뉴 그룹 목록을 조회할 수 있다.
  
- 메뉴
  - [ ] 메뉴는 메뉴ID(UUID), 이름, 가격, 메뉴그룹, 노출/숨김여부, 메뉴구성상품목록 정보를 관리한다.
    - [ ] 메뉴ID는 고유한 UUID를 생성해서 사용한다.
    - [ ] 메뉴의 이름을 관리한다.
    - [ ] 메뉴의 가격정보를 관리한다.
    - [ ] 메뉴의 노출 / 숨김 여부를 관리한다.
    - [ ] 메뉴에 포함된 구성상품 목록을 관리한다.
  - [ ] 메뉴를 등록할 수 있다.
    - [ ] 메뉴를 등록할때 메뉴이름, 메뉴그룹ID, 가격, 구성상품정보를 필수로 입력해야한다. 
      - [ ] 메뉴이름은 공백/욕설을 제외한 최대 255자 이내로 입력한다.
      - [ ] 가격은 0원 이상으로 입력한다.
      - [ ] 메뉴그룹 ID를 필수로 입력한다.
      - [ ] 메뉴구성상품은 최소 1개 이상 입력한다.
        - [ ] 메뉴구성상품은 상품ID와 수량정보를 필수로 입력해야한다.
        - [ ] 상품ID는 DB에 등록된 상품의 ID를 입력한다.
        - [ ] 수량은 0 이상 값을 입력한다.
        - [ ] 메뉴의 가격과 메뉴구성상품의 총 금액이 일치해야한다.
  - [ ] 메뉴 가격을 변경할 수 있다.
    - [ ] 변경하려는 가격은 0원 이상이어야 한다.
    - [ ] 계산한 메뉴구성상품들의 총 합 금액과 사용자가 입력한 금액(Price)가 일치해야한다. 
  - [ ] 메뉴를 노출하거나 숨길 수 있다.
    - [ ] 계산한 메뉴구성상품들의 총 합 금액과 메뉴에 등록된 금액(price)가 같으면 노출처리한다.
    - [ ] 숨김처리는 등록된 메뉴인지 확인 후 숨김처리한다.
  - [ ] 메뉴 목록을 조회할 수 있다.

- 상품
  - [ ] 상품은 상품ID(UUID), 상품이름, 가격정보를 관리한다.
    - [ ] 상품ID는 고유한 UUID를 생성해서 사용한다.
  - [ ] 상품을 등록할 수 있다.
    - [ ] 가격은 0원 이상으로 입력한다.
    - [ ] 상품이름은 공백/욕설을 제외한 최대 255자 이내로 입력한다.
  - [ ] 상품의 가격을 변경할 수 있다.
    - [ ] 변경하려는 가격은 0원 이상으로 입력한다.
  - [ ] 상품목록을 조회할 수 있다.
  

 # 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
