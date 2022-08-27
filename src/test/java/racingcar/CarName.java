package racingcar;

import java.util.Objects;

public class CarName {
    private static final long MAX_LENGTH = 5;
    private static final String LENGTH_CHECK_MESSAGE = "자동차 이름은 5글자를 넘을 수 없습니다";
    private static final String NULL_CHECK_MESSAGE = "자동차 이름은 null이 될 수 없습니다";

    private final String value;

    private CarName(String value) {
        this.value = validate(value);
    }

    public static CarName of(String value) {
        return new CarName(value);
    }

    private String validate(String value) {
        Objects.requireNonNull(value, NULL_CHECK_MESSAGE);
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(LENGTH_CHECK_MESSAGE);
        }
        return value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
