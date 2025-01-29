package calculator;

import java.util.List;

public class Calculator {

    private final List<CalculateStrategy> calculationStrategies;

    public Calculator() {
        this.calculationStrategies = List.of(
                new NoSeparatorStrategy(),
                new CustomSeparatorStrategy(),
                new BaseSeparatorStrategy()
        );
    }

    public int calculate(String input) {
        return calculationStrategies.stream()
                .filter(calculationStrategy -> calculationStrategy.canCalculate(input))
                .findFirst()
                .map(calculationStrategy -> calculationStrategy.calculate(input))
                .orElseThrow(() -> new RuntimeException("알맞는 계산기를 찾지 못했습니다."));
    }
}
