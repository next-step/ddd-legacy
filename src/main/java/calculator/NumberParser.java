package calculator;

import calculator.vo.PositiveNumbers;
import calculator.vo.Tokens;

public record NumberParser() {

    public PositiveNumbers parse(Tokens tokens) {
        return PositiveNumbers.fromTokens(
            tokens.token()
        );
    }
}
