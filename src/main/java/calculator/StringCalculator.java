package calculator;

import calculator.vo.Numbers;
import calculator.vo.Tokens;

public record StringCalculator(
    DelimiterParser delimiterParser,
    NumberParser numberParser
) {

    public int add(String input) {
        Tokens tokens = delimiterParser.parse(input);

        Numbers numbers = numberParser.parse(tokens);

        return numbers.sum();
    }
}
