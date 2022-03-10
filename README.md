# 키친포스

## 요구 사항

## 용어 사전

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |
|  |  |  |

## 모델링

-------------

# STEP1

## 고민

### Number.java

- String을 분석하여 int로 변환할 때 제약사항을 검증 역할을 수행하는 클래스로 설계.
- 문제는 덧셈 연산 책임을 부여해도 되는지에 대해 고민이 됨
    - 고민의 이유는 다음과 같음
        1. 덧셈 연산을 통해 의도된 값을 가지는 Number 객체를 생성해도 getValue()를 통해 내부의 상태를 외부에 제공해야함
        2. 위의 이유로 인해 Number의 add(Number)를 호출하기 보단 getValue()를 통해 값을 가지고 수행해도 된다는 의미로 보여지지 않나 고민이 됨(
           전혀 그런 의도가 아니기 때문)
    - 이런 문제는 어떻게 하는게 좋을지?

### TokenFactory.java

- TokenFactory에게 Delimiter 생성 및 text 분해의 책임을 부여함
- 하지만 현재의 구조는 기본 구분자를 사용하는 경우와 커스텀 구분자를 사용하는 경우를 모두 책임지고 있기 때문에 SRP를 준수하는 디자인이라고 보기 어렵다고 생각됨
    - 다른 분해 방법이 추가 되면 더 상황은 심각해질 것임
- 이런 상황을 어떻게 해결할 수 있을지?
    - factory 내부의 구조를 다음과 같이 개선하면 도움이 될지?
        - text의 상태를 보고 분해 전략 객체를 선택하는 selector 정의
        - 각 분해 전략을 구현할 클래스 정의 및 객체화
        - 아래의 코드 예시 참고
            - 특히 DelimitStrategy.java의 `List<String> delimit(final String text)`가 적절한 디자인인지도 궁금함

```java
interface DelimitStrategy {

    /**
     * 여기가 어려운 부분인데
     * 처음에는 단순히 Delimiter.java를 생성하는 방식을 DelimitStrategy에서 책임지게 하고
     * 생성된 Delimiter.java들을 이용하여 pattern을 만들고 split을 하는 것을 TokenFactory에서 하려 했으나 커스텀 구분자에서는 문제가 있음.
     *
     * 커스텀 구분자의 경우엔 언제나 text의 앞부분을 제거하고 시작해야 하는데
     * 단순히 Delimiter.java들을 통해 얻게 되는 pattern으로는 앞부분을 자를 수 없고
     * text의 앞부분을 어디까지 잘라야 하는지가 TokenFactory에 공개 되어야 하는 문제가 발생
     *
     * 이는 커스텀 구분자 핵심 규칙에 대한 캡슐레이션이 파괴됨을 의미하고
     * TokenFactory와 CustomDelimitStrategy의 결합도 증가 및 산탄총 수술의 시초가 된다고 생각함
     * (문제 되는 맥락의 처리가 필요한 경우들이 늘어나면 늘어날 수록 상황은 악화될테니)
     *
     * 따라서 현재의 디자인에서는 Delimiter.java만 반환하는게 아니라
     * 아에 분해된 결과를 반환
     */
    List<String> delimit(final String text);

    boolean acceptable(final String text);
}

class CustomDelimitStrategy implements DelimitStrategy {
    // ...
}

class AnotherCustomDelimitStrategy implements DelimitStrategy {
    // ...
}

class DefaultDelimitStrategy implements DelimitStrategy {
    // ...
}

class Selector {

    private final List<DelimitStrategy> strategies;

    Selector() {
        strategies = new ArrayList();
        strategies.add(new CustomDelimitStrategy());
        strategies.add(new AnotherCustomDelimitStrategy());
        strategies.add(new DefaultDelimitStrategy());
    }

    Optional<DelimitStrategy> select(final String text) {
        return strategies.stream()
            .filter(strategy -> strategy.acceptable(text))
            .findFirst();
    }
}
```