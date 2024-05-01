package calculator;

public class StringCalculator {
    public int add(String input) {
        return NumbersParserUtils.parse(input)
                .sum()
                .value();
    }
}
