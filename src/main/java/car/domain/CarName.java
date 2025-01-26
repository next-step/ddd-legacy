package car.domain;


import java.util.Objects;

public record CarName(String name) {
    private static final int NAME_LIMIT_LENGTH = 5;
    private static final String OVER_NAME_ERROR_MESSAGE = "이름은 %d글자 초과일 수 없습니다.";

    public CarName {
        validateNameLength(name);
    }

    private void validateNameLength(final String name) {
        if (name.length() > NAME_LIMIT_LENGTH) {
            throw new IllegalArgumentException(String.format(OVER_NAME_ERROR_MESSAGE, NAME_LIMIT_LENGTH));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarName carName = (CarName) o;
        return Objects.equals(name, carName.name);
    }

    @Override
    public String toString() {
        return "CarName{" +
                "name='" + name + '\'' +
                '}';
    }

}
