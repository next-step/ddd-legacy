package calculator;

import java.util.Arrays;

public class StringCalculator {
    private static final int DEFAULT_RETURN_VALUE = 0;
    private static final String VALID_TOKEN_REGEX = "^[0-9]+$";
    private static final String INVALID_TOKEN_ERROR_MESSAGE = "숫자 이외의 값 또는 음수는 전달할 수 없습니다: \"%s\"";
    private static final String ERROR_TOKEN_MESSAGE_DELIMITER = "\", \"";

    public int add(final String text) throws RuntimeException {
        if (text == null) {
            return DEFAULT_RETURN_VALUE;
        }

        StringTokenizer stringTokenizer = StringTokenizer.of(text);

        if (stringTokenizer.isEmpty()) {
            return DEFAULT_RETURN_VALUE;
        }

        final String[] tokens = stringTokenizer.tokenize();
        validateTokens(tokens);
        return calculate(tokens);
    }

    private void validateTokens(String[] tokens) throws RuntimeException {
        final String[] exceptTokens = Arrays.stream(tokens)
                .filter(token -> !token.matches(VALID_TOKEN_REGEX))
                .toArray(String[]::new);

        if (exceptTokens.length > 0) {
            String errorMessage = String.format(
                    INVALID_TOKEN_ERROR_MESSAGE,
                    String.join(ERROR_TOKEN_MESSAGE_DELIMITER, exceptTokens)
            );
            throw new RuntimeException(errorMessage);
        }
    }

    private int calculate(String[] tokens) {
        return Arrays.stream(tokens)
                .mapToInt(Integer::parseInt)
                .sum();
    }
}
