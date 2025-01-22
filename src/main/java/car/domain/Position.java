package car.domain;

import java.util.Objects;

public class Position {
    private static final String NOT_VALIDATE_NUMBER_ERROR_MESSAGE = "%d보다 큰 숫자를 입력해야합니다.";

    private static final int MIN_NUMBER = 0;
    private static final int INCREMENT = 1;

    private final int number;

    private Position(final int number) {
        this.number = number;
        isValidateNumber(number);
    }

    private void isValidateNumber(final int number){
        if(MIN_NUMBER > number) {
            throw new IllegalArgumentException(String.format(NOT_VALIDATE_NUMBER_ERROR_MESSAGE, MIN_NUMBER));
        }
    }

    public static Position from(final int number) {
        return new Position(number);
    }

    public Position move(final boolean movable) {
        if(movable) {
            return new Position(this.number+INCREMENT);
        }
        return this;
    }


    public int getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position that = (Position) o;
        return number == that.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    @Override
    public String toString() {
        return "MoveNumber{" +
                "number=" + number +
                '}';
    }

}
