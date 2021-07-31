package calculator;

import java.util.Objects;

public class StringCalculator {
    private static final int DEFAULT_RETURN_VALUE = 0;
    private static final StringTokenizer stringTokenizer = new StringTokenizer();
    private static final String VALID_TOKEN_REGEX = "^[0-9]+$";
    private static final String INVALID_TOKEN_ERROR_MESSAGE = "숫자 이외의 값 또는 음수는 전달할 수 없습니다: \"%s\"";
    private static final String ERROR_TOKEN_MESSAGE_DELIMITER = "\", \"";

    public int add(final String text) throws RuntimeException {
        if (Objects.isNull(text)) {
            return DEFAULT_RETURN_VALUE;
        }

        final StringOperands stringOperands = stringTokenizer.tokenize(text);
        if (stringOperands.isEmpty()) {
            return DEFAULT_RETURN_VALUE;
        }

        validateTokens(stringOperands);
        return calculate(stringOperands);
    }

    private void validateTokens(StringOperands stringOperands) throws RuntimeException {
        final String[] exceptTokens = stringOperands.stream()
                .filter(operand -> !operand.matches(VALID_TOKEN_REGEX))
                .toArray(String[]::new);

        if (exceptTokens.length > 0) {
            String errorMessage = String.format(
                    INVALID_TOKEN_ERROR_MESSAGE,
                    String.join(ERROR_TOKEN_MESSAGE_DELIMITER, exceptTokens)
            );
            throw new RuntimeException(errorMessage);
        }
    }

    private int calculate(StringOperands stringOperands) {
        return stringOperands.stream()
                .mapToInt(Integer::parseInt)
                .sum();
    }
}
