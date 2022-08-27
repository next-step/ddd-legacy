package racingcar;

import java.util.Objects;

public class CarName {
    private String value;

    private CarName(String value) {
        this.value = validate(value);
    }

    public static CarName of(String value) {
        return new CarName(value);
    }

    private String validate(String value) {
        Objects.requireNonNull(value);
        if (value.length() > 5) {
            throw new IllegalArgumentException("자동차 이름은 5글자를 넘을 수 없습니다");
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
