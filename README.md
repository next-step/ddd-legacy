# 키친포스

## 요구 사항
### 상품 (Product)
* [ ] 상품을 생성한다.  
  * [ ] 상품 생성 시 가격은 필수이다.  
    * [ ] 상품 가격은 0보다 커야한다.      
  * [ ] 상품 생성 시 상품의 이름은 필수이다.  
    * [ ] 상품 이름은 중복 가능하다.  

* [ ] 생성된 모든 상품들을 조회한다.  
  * 상품 식별자  
  * 상품 이름  
  * 상품 가격

### 메뉴 그룹 (MenuGroup)  
* [ ] 메뉴 그룹을 생성한다.  
  * [ ] 메뉴 그룹 이름은 필수이다.  
* [ ] 생성된 메뉴 그룹들을 조회한다.  
  * [ ] 메뉴 그룹 식별자  
  * [ ] 메뉴 그룹 이름  
  
### 메뉴 (Menu)  
* [ ] 메뉴를 생성한다.  
  * [ ] 메뉴 생성 시 메뉴의 가격은 필수이다.    
    * [ ] 메뉴의 가격은 0보다 작을 수 없다.  
    * [ ] 메뉴의 가격은 해당 메뉴의 속한 모든 종류별 상품 가격의 총합보다 클 수 없다.  
      > 상품 가격의 총합 : 상품 가격 * 상품 갯수  
  * [ ] 메뉴 생성 시 메뉴는 1개의 메뉴 그룹에 소속되어야 한다.   
    * [ ] 메뉴가 소속될 메뉴 그룹은 기존에 생성되어 있어야 한다.  
  * [ ] 메뉴 생성 시 메뉴는 상품을 1 종류 이상 갖는다.  
    * [ ] 메뉴가 가질 수 있는 상품은 기존에 생성되어 있어야 한다.  
    * [ ] 상품의 종류는 중복 가능하다.  
    * [ ] 상품의 종류 별로 갯수를 1개 이상 갖는다.  

* [ ] 생성된 모든 메뉴들을 조회한다.  
  * 메뉴 식별자  
  * 메뉴 이름  
  * 메뉴 가격  
  * 메뉴 그룹 식별자  
  * 메뉴에 속한 모든 상품들          
    * 메뉴 식별자  
    * 상품 식별자  
    * 상품 갯수      

### 테이블 (Table / OrderTable)  
* [ ] 테이블을 생성한다.  
  * [ ] 빈 테이블 여부는 필수이다.   
  * [ ] 테이블의 손님 수는 필수이다.  
* [ ] 생성된 모든 테이블을 조회한다.  
  * 테이블 식별자  
  * 테이블 그룹 식별자  
  * 테이블의 손님 수  
  * 빈 테이블 여부  
* [ ] 테이블을 빈 상태로 변경한다.  
  * [ ] 테이블 그룹이 존재해야 한다.  
  * [ ] 해당 테이블의 주문이 조리중이나 식사중 상태여야 한다.  
* [ ] 테이블에 있는 손님 수를 변경한다.  
  * [ ] 테이블에 있는 손님 수는 0보다 작을 수 없다.  
  * [ ] 테이블은 비어있는 상태가 아니어야 한다.  
 

      
## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
