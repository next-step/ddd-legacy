@Test
Feature: 상품을 생성

  Scenario: 상품이 생성된다
    Given 상품 이름은 아메리카노 이고 상품 가격은 1000원
    When 상품 생성 요청
    Then 상품 생성 성공

  Scenario: 상품 생성실패한다 - 상품 이름이 없는 경우
    Given 상품 이름은 null 이고 상품 가격은 1000원
    When 상품 생성 요청
    Then 상품 생성 실패

  Scenario: 상품 생성실패한다 - 상품 가격이 0원 미만인 경우
    Given 상품 이름은 아메리카노 이고 상품 가격은 -1원
    When 상품 생성 요청
    Then 상품 생성 실패

  Scenario: 상품 생성실패한다 - 욕설 단어가 포함된 경우
    Given 상품 이름은 fuck메리카노 이고 상품 가격은 1000원
    When 상품 생성 요청
    Then 상품 생성 실패
