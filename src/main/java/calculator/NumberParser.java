package calculator;

import calculator.vo.Numbers;
import calculator.vo.Tokens;

public record NumberParser() {

    public Numbers parse(Tokens tokens) {
        return Numbers.fromTokens(tokens.token());
    }
}
