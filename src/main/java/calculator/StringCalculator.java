package calculator;

public class StringCalculator {

    private final CalculateStrategyManager calculateStrategyManager;

    public StringCalculator() {
        this.calculateStrategyManager = new CalculateStrategyManager();
    }

    public int add(final String text) {
        CalculateStrategy strategy = calculateStrategyManager.findStrategy(text);
        return strategy.calculate(text);
    }

}