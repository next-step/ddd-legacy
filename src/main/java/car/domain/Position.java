package car.domain;

public record Position(int number) {
    private static final String NOT_VALIDATE_NUMBER_ERROR_MESSAGE = "%d보다 큰 숫자를 입력해야합니다.";

    private static final int MIN_NUMBER = 0;
    private static final int INCREMENT = 1;

    public Position {
        isValidateNumber(number);
    }

    private void isValidateNumber(final int number) {
        if (MIN_NUMBER > number) {
            throw new IllegalArgumentException(String.format(NOT_VALIDATE_NUMBER_ERROR_MESSAGE, MIN_NUMBER));
        }
    }


    public Position move(final boolean movable) {
        if (movable) {
            return new Position(this.number + INCREMENT);
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position that = (Position) o;
        return number == that.number;
    }

    @Override
    public String toString() {
        return "MoveNumber{" +
                "number=" + number +
                '}';
    }

}
