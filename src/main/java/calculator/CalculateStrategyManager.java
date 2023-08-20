package calculator;

import java.util.List;

public class CalculateStrategyManager {

    private final List<CalculateStrategy> calculateStrategies;

    public CalculateStrategyManager() {
        this.calculateStrategies = List.of(
                new NullOrEmptyCalculateStrategy(),
                new NumberPatternCalculateStrategy(),
                new SeparatorCalculateStrategy(),
                new CustomSeparatorCalculateStrategy()
        );
    }

    public CalculateStrategy findStrategy(final String text) {
        return calculateStrategies.stream()
                .filter(strategy -> strategy.isTarget(text))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 형식입니다."));
    }
    
}
