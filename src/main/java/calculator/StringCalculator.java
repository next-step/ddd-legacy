package calculator;


public class StringCalculator {
    private final DelimiterParser delimiterParser;

    public StringCalculator() {
        this.delimiterParser = DelimiterParser.getInstance();
    }

    public int add(String input) {
        return delimiterParser.parse(input).sum().value();
    }
}
