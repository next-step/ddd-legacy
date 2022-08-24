package racingcar;

import java.util.Objects;

public class Car {

    private static final int MAXIMUM_NAME_LENGTH = 5;
    private static final int MOVE_CONDITION = 4;
    private final String name;
    private int position;

    public Car(final String name) {
        this(name, 0);
    }

    public Car(final String name, final int position) {
        validateName(name);
        this.name = name;
        this.position = position;
    }

    public void validateName(final String name) {
        if (Objects.isNull(name) || name.isBlank()) {
            throw new IllegalArgumentException("자동차의 이름은 빈값일 수 없습니다.");
        }

        if (name.length() > MAXIMUM_NAME_LENGTH) {
            throw new IllegalArgumentException("자동차의 이름은 5글자를 넘길 수 없습니다.");
        }
    }

    public void move(final MovingStrategy movingStrategy) {
        if (movingStrategy.movable()) {
            position++;
        }
    }

    public int getPosition() {
        return position;
    }
}
