package racingcar;

import java.util.Objects;

public class CarName {
    private static final String ERROR_MESSAGE = "자동차 이름은 5글자를 넘을 수 없다.";
    private String name;

    public CarName(String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException(ERROR_MESSAGE);
        }
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CarName carName = (CarName) o;
        return Objects.equals(name, carName.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
