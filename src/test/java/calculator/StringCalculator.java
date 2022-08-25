package calculator;

import java.util.Arrays;

public class StringCalculator {
    private final Parser parser;

    public StringCalculator(final Parser parser) {
        this.parser = parser;
    }

    private int checkNumberFromToken(String token) {
        int value = Integer.parseInt(token);
        if (value < 0) throw new RuntimeException("값은 0보다 작을 수 없습니다.");
        return value;
    }

    public int add(String text) {
        if (text == null || text.isEmpty()) return 0;

        String[] tokens = parser.execute(text).getTokens();
        if (tokens == null || tokens.length <= 0)
            throw new RuntimeException("입력된 값이 없습니다.");

        return Arrays.asList(tokens)
                .stream()
                .map(x -> checkNumberFromToken(x))
                .reduce(0, Integer::sum)
                .intValue();
    }
}