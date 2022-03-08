package racingcar;

import static java.util.Objects.requireNonNull;

public final class Car {

    private final String name;
    private int position;

    public Car(final String name, final int position) {
        this.name = validateName(name);
        this.position = position;
    }

    private String validateName(final String name) {
        final int minimumNameLength = 5;

        requireNonNull(name);

        if (name.length() < minimumNameLength) {
            throw new IllegalArgumentException(
                String.format("name length must be longer than %d", minimumNameLength));
        }

        return name;
    }

    public void move(final MovingStrategy movingStrategy) {
        requireNonNull(movingStrategy);

        if (movingStrategy.movable()) {
            position++;
        }
    }

    public int getPosition() {
        return position;
    }
}
