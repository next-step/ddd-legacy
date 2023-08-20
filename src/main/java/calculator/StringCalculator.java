package calculator;

import java.util.List;

public class StringCalculator {

    private final List<CalculateStrategy> calculateStrategies = List.of(
            new NullOrEmptyCalculateStrategy(),
            new NumberPatternCalculateStrategy(),
            new SeparatorCalculateStrategy(),
            new CustomSeparatorCalculateStrategy()
    );

    public int add(final String text) {
        return calculateStrategies.stream()
                .filter(strategy -> strategy.isTarget(text))
                .findFirst()
                .map(strategy -> strategy.calculate(text))
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 형식입니다."));
    }

}

