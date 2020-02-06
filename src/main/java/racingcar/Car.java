package racingcar;

import org.apache.logging.log4j.util.Strings;

public class Car {
    private final String name;
    private int position;

    Car(final String name) {
        this(name, 0);
    }

    Car(final String name, final int position) {
        validate(name);
        this.name = name;
        this.position = position;
    }

    void move(final RandomMovingStrategy movingStrategy) {
        if (movingStrategy.movable()) {
            position++;
        }
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    private void validate(final String name) {
        if (Strings.isBlank(name) || name.length() > 5) {
            throw new IllegalArgumentException();
        }
    }
}
