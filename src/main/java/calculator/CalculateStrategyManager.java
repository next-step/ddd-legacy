package calculator;

import java.util.List;

public class CalculateStrategyManager {

    private static final List<CalculateStrategy> calculateStrategies = List.of(
            new NullOrEmptyCalculateStrategy(),
            new NumberPatternCalculateStrategy(),
            new SeparatorCalculateStrategy(),
            new CustomSeparatorCalculateStrategy()
    );

    public static CalculateStrategy findStrategy(final String text) {
        return calculateStrategies.stream()
                .filter(strategy -> strategy.isTarget(text))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 형식입니다."));
    }
    
}
