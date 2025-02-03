@Test
Feature: 메뉴 그룹을 생성

  Scenario: 메뉴 그룹이 생성된다
    Given 메뉴 그룹 이름은 "추천메뉴"
    When 메뉴 그룹 생성 요청
    Then 메뉴 그룹 생성 성공

  Scenario: 메뉴 그룹 생성실패한다 - 메뉴 그룹 이름이 없는 경우
    Given 메뉴 그룹 이름은 ""
    When 메뉴 그룹 생성 요청
    Then 메뉴 그룹 생성 실패
