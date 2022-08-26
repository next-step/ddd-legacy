package calculator;

import java.util.Arrays;

public class StringCalculator {

    private final Parser parser;

    public StringCalculator(final Parser parser) {
        this.parser = parser;
    }

    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        String[] tokens = parser.execute(text);
        if (isEmptyTokens(tokens)) {
            throw new RuntimeException("입력된 값이 없습니다.");
        }

        return Arrays.stream(tokens)
            .map(token -> getNumberFromToken(token))
            .reduce(0, Integer::sum);
    }

    private boolean isEmptyTokens(String[] tokens) {
        return (tokens == null || tokens.length <= 0);
    }

    private int getNumberFromToken(String token) {
        if (!isNumeric(token)) {
            throw new RuntimeException("숫자가 아닙니다.");
        }

        int value = Integer.parseInt(token);
        if (value < 0) {
            throw new RuntimeException("값은 0보다 작을 수 없습니다.");
        }
        return value;
    }

    private boolean isNumeric(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        return text.matches("\\d+");
    }
}
