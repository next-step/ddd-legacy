# 키친포스

## 퀵 스타트

```sh
cd docker
docker compose -p kitchenpos up -d
```

## 요구 사항
- [x] 자동차 이름은 5 글자를 넘을 수 없다.
- [x] 5 글자가 넘는 경우, IllegalArgumentException이 발생한다.
- [x] 자동차가 움직이는 조건은 0에서 9 사이의 무작위 값을 구한 후, 무작위 값이 4 이상인 경우이다.
- [x] JUnit 5의 @DisplayName을 사용하여 테스트 메서드의 의도를 한글로 표현한다.
- [x] JUnit의 Assertions이 아닌 AssertJ의 Assertions를 사용한다.
- [] 개발자는 코드를 작성하면서 확장성(scalability)에 대한 고민을 항상 해야 한다. 자동차가 다양한 방법으로 움직일 수 있게 구현한다.
- [x] 자동차가 조건에 따라 움직였는지 움직이지 않았는지 테스트하고자 할 때는 isBetween(), isGreaterThan(), isLessThan() 등은 사용하지 않는다.

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링
