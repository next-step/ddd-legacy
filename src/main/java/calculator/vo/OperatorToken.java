package calculator.vo;

import calculator.type.OperatorType;
import calculator.type.TokenType;

public class OperatorToken implements Token {
    public static final String DEFAULT_OPERATOR_TOKEN = ",";
    public final OperatorType operatorType;

    private OperatorToken(OperatorType operatorType) {
        this.operatorType = operatorType;
    }

    public static Token of(String value) {
        switch (value) {
            case DEFAULT_OPERATOR_TOKEN:
            case ":":
                return new OperatorToken(OperatorType.PLUS);

        }

        throw new IllegalArgumentException("Unexpected value: " + value);
    }

    @Override
    public TokenType getType() {
        return TokenType.OPERATOR;
    }
}
