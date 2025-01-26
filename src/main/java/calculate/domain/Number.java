package calculate.domain;

import java.util.Objects;

public class Number {
    private static final String NUMBER_FORMAT_ERROR_MESSAGE = "문자열 계산기에는 음수가 입력될 수 없습니다.";

    private final int value;

    public Number(final String value) {
        this.value = isValidate(makeNumber(value));
    }

    public Number(final int value) {
        this.value = isValidate(value);
    }

    private int makeNumber(final String value) {
        if(value== null || value.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(value);
    }

    private int isValidate(int value) {
        if(value < 0) {
            throw new RuntimeException(NUMBER_FORMAT_ERROR_MESSAGE);
        }
        return value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Number number1 = (Number) o;
        return value == number1.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}
