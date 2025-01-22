package car.domain;


import java.util.Objects;

public class CarName {
    private static final int NAME_LIMIT_LENGTH = 5;
    private static final String OVER_NAME_ERROR_MESSAGE = "이름은 %d글자 초과일 수 없습니다.";

    private final String name;

    private CarName(final String name) {
        this.name = name;
        validateNameLength(name);
    }

    public static CarName from(final String name) {
        return new CarName(name);
    }

    private void validateNameLength(final String name) {
        if(name.length() > NAME_LIMIT_LENGTH) {
            throw new IllegalArgumentException(String.format(OVER_NAME_ERROR_MESSAGE, NAME_LIMIT_LENGTH));
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarName carName = (CarName) o;
        return Objects.equals(name, carName.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "CarName{" +
                "name='" + name + '\'' +
                '}';
    }

}
