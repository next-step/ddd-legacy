package calculator;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StringOperand {
    private static final String VALID_TOKEN_REGEX = "^[0-9]+$";
    private static final String INVALID_TOKEN_ERROR_MESSAGE = "숫자 이외의 값 또는 음수는 전달할 수 없습니다: \"%s\"";
    private static final Map<String, StringOperand> INSTANCE_MAP = new HashMap<>();

    private final String operand;

    private StringOperand(final String operand) {
        this.operand = operand;
    }

    public static StringOperand of(final String operand) {
        validateOperand(operand);

        if (!INSTANCE_MAP.containsKey(operand)) {
            INSTANCE_MAP.put(operand, new StringOperand(operand));
        }
        return INSTANCE_MAP.get(operand);
    }

    private static void validateOperand(final String operand) throws RuntimeException {
        if (!operand.matches(VALID_TOKEN_REGEX)) {
            throw new RuntimeException(String.format(INVALID_TOKEN_ERROR_MESSAGE, operand));
        }
    }

    public int parseInt() {
        return Integer.parseInt(operand);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (Objects.isNull(o) || getClass() != o.getClass()) {
            return false;
        }

        StringOperand that = (StringOperand) o;
        return Objects.isNull(operand) ? Objects.isNull(that.operand) : operand.equals(that.operand);
    }

    @Override
    public int hashCode() {
        return Objects.isNull(operand) ? 0 : operand.hashCode();
    }
}
