# 키친포스

## 요구 사항
### 식당에서 사용하는 POS 프로그램을 구현한다.
  
### 메뉴
* 고객이 주문할 수 있는 음식을 메뉴라고 한다.  
* 메뉴는 이름, 가격, 여러개의 제품으로 구성되어 있다.
* 메뉴를 생성할 수 있다.
    * 이름, 가격, 메뉴그룹은 반드시 있어야 한다.
    * 메뉴가 속한 메뉴그룹은 반드시 존재 해야한다.
    * 메뉴가 사용하는 제품은 반드시 존재 해야한다.
    * 메뉴가격은 0 이상이다.
    * 메뉴의 가격은 메뉴를 구성하는 제품들의 가격 합으로 정해진다.
    * 메뉴의 가격은 제품가격을 합한 가격 이하여야 한다. 
    * 하나의 카테고리(메뉴그룹)에 포함되어야 한다.
* 메뉴 목록을 볼 수 있다.

### 제품  
* 메뉴를 구성하고 있는 실제 음식을 제품이라고 한다.   
* 제품이름, 가격으로 구성된다.
* 제품을 생성할 수 있다.
    * 제품가격은 0원 이상이다.
    * 제품가격과 제품이름은 반드시 있어야 한다.
* 제품 목록을 볼 수 있다.

### 메뉴그룹 
* 여러개의 메뉴를 묶어 카테고리를 만들 수 있는데 이를 메뉴그룹이라 한다.  
예를 들면 새로운 메뉴들을 묶어 '신메뉴' 라는 카테고리를 만들 수 있고, 이 카테고리가 메뉴그룹이다.
* 메뉴그룹은 이름을 가지고 있다.
* 메뉴그룹을 생성할 수 있다.
    * 메뉴 그룹의 이름은 반드시 있어야 한다.
* 메뉴그룹 목록을 볼 수 있다.

### 주문
* 주문은 테이블과 메뉴와 수량으로 이루어진다.
* 주문을 생성할 수 있다.
    * 주문된 메뉴가 1개 이상 있어야 한다.
    * 주문된 메뉴는 반드시 팔고 있는 메뉴여야 한다.
    * 주문된 테이블이 식당에 존재하는 테이블이어야 한다.
    * 주문된 일자와 시간을 기록한다.
* 주문은 요리중, 식사, 완료 상태를 가진다.
* 주문의 상태를 변경할 수 있다.
    * 주문 상태가 완료로 변경된 것은 상태를 변경할 수 없다.
* 주문 목록을 볼 수 있다.

## 테이블
* 식당에 있는 테이블을 이야기 한다.
* 테이블을 생성할 수 있다.
    * 테이블에 앉은 손님의 수, 테이블이 비어있는 지 여부는 반드시 들어가야한다.
* 테이블은 0 또는 1개의 테이블 그룹에 속할 수 있다.
* 테이블이 비어있는 지 설정이 가능하다
    * 변경하려는 테이블이 식당에 존재 해야한다.
    * 테이블에 요리중 이거나 식사 상태인 주문이 있다면 설정 할 수 없다.
* 테이블에 앉은 손님의 수를 변경할 수 있다. 
    * 변경할 수 있는 손님의 수는 0 이상이다.
    * 손님의 수를 변경하려는 테이블이 식당에 존재 해야한다.

## 테이블 그룹
* 단체 손님들이 왔을 때와 같이 테이블을 묶어서 관리를 할 수 있다. 테이블 이 묶인 이 단위를 테이블 그룹이라 한다.  
* 테이블 그룹을 생성할 수 있다.
    * 테이블 그룹은 2개 이상의 테이블로 구성된다.
    * 테이블 그룹을 만들려는 테이블은 식당에 존재 해야한다.
    * 테이블 그룹이 만들어진 일자와 시간을 저장한다.
* 테이블 그룹을 삭제할 수 있다.
    * 테이블 그룹에 속한 테이블에 요리중, 식사 상태 인 주문이 있다면 삭제할 수 없다. 
