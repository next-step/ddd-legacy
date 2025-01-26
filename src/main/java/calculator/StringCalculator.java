package calculator;

import calculator.vo.PositiveNumbers;
import calculator.vo.Tokens;

public record StringCalculator(
    DelimiterParser delimiterParser,
    NumberParser numberParser
) {

    public int add(String input) {
        Tokens tokens = delimiterParser.parse(input);

        PositiveNumbers positiveNumbers = numberParser.parse(tokens);

        return positiveNumbers.sum();
    }
}
