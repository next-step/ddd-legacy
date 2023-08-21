package calculator.vo;

import calculator.type.TokenType;

public class ValueToken implements Token {
    public final Integer value;

    private ValueToken(Integer value) {
        this.value = value;
    }

    public static Token of(String value) {
        return new ValueToken(Integer.valueOf(value));
    }

    @Override
    public TokenType getType() {
        return TokenType.VALUE;
    }
}
