package calculator;

import static java.util.Objects.requireNonNull;

import java.util.regex.Pattern;

final class Number {

    private static final Pattern VALID_PATTERN = Pattern.compile("^-?[0-9]*$");

    private final int value;

    /**
     * @throws NumberFormatException 십진수 숫자가 아닌 문자가 포함되거나 {@link Integer#MAX_VALUE} 이상의 value일 때.
     *                               +기호도 허용하지 않는다.
     * @throws RuntimeException      value가 음수일 때
     */
    static Number parse(final String value) throws NumberFormatException {
        checkValue(value);

        return new Number(Integer.parseInt(value));
    }

    private static void checkValue(final String value) {
        requireNonNull(value);

        if (!VALID_PATTERN.matcher(value).find()) {
            throw new NumberFormatException("Only decimal digit is allowed. value: " + value);
        }
    }

    private Number(final int value) {
        if (value < 0) {
            throw new RuntimeException("negative value. value: " + value);
        }

        this.value = value;
    }

    int getValue() {
        return value;
    }

    Number add(Number other) {
        requireNonNull(other);

        return new Number(value + other.value);
    }
}
