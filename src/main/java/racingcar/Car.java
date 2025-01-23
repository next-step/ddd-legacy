package racingcar;

public class Car {
    private static final int LIMIT_NAME_LENGTH = 5;
    private static final int MINIMUM_MOVABLE_CONDITION = 4;

    private final String name;
    private int position;

    public Car(final String name) {
        if (name.length() > LIMIT_NAME_LENGTH) {
            throw new IllegalArgumentException(String.format("차의 이름은 %d글자를 초과할 수 없습니다", LIMIT_NAME_LENGTH));
        }
        this.name = name;
    }

    public void move(final int condition) {
        if (condition >= MINIMUM_MOVABLE_CONDITION) {
            position++;
        }
    }

    public int getPosition() {
        return position;
    }
}
