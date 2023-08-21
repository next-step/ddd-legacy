package calculator;

public class StringCalculator {

    public int add(final String text) {
        CalculateStrategy strategy = CalculateStrategyManager.findStrategy(text);
        return strategy.calculate(text);
    }

}