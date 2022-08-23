package racingcar;

import java.util.Objects;

public class CarName {

    private final String value;

    public CarName(String value) {
        this.value = value;
        validateNull(this.value);
        validateBlank(this.value);
        validateLength(this.value);
    }

    private void validateNull(String value) {
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("자동차 이름은 null 일 수 없습니다.");
        }
    }

    private void validateBlank(String value) {
        if (value.isBlank()) {
            throw new IllegalArgumentException("자동차 이름은 비어 있을 수 없습니다.");
        }
    }

    private void validateLength(String value) {
        if (value.length() > 5) {
            throw new IllegalArgumentException("자동차 이름은 5글자를 넘을 수 없습니다.");
        }
    }

    public String getValue() {
        return value;
    }
}
