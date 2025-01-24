package calculator;

import calculator.vo.Numbers;
import calculator.vo.Tokens;

public record StringCalculator(
    InputValidator inputValidator,
    DelimiterParser delimiterParser,
    NumberParser numberParser
) {

    public int add(String input) {
        inputValidator.validate(input);

        Tokens tokens = delimiterParser.parse(input);

        Numbers numbers = numberParser.parse(tokens);

        return numbers.sum();
    }
}
