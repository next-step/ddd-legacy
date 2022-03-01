package racingcar;

import java.util.Objects;

public final class Car {

    private static final int NAME_MAX_RANGE = 5;

    private final String name;
    private int position;

    public Car(String name) {
        validate(name);
        this.name = name;
    }

    private void validate(String name) {
        if (Objects.isNull(name) || name.trim().length() > NAME_MAX_RANGE) {
            throw new IllegalArgumentException();
        }
    }

    public void move(final MovableStrategy strategy) {
        if (strategy.movable()) {
            position++;
        }
    }

    public int getPosition() {
        return position;
    }
}
