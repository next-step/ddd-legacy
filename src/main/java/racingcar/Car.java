package racingcar;

import java.util.Objects;

import org.apache.logging.log4j.util.Strings;

public class Car {
    static final int DEFAULT_POSITION = 0;
    static final int POINT_VARIATION_OF_MOVING = 1;
    private final String name;
    private int position;

    Car(final String name) {
        this(name, DEFAULT_POSITION);
    }

    Car(final String name, final int position) {
        validate(name);
        this.name = name;
        this.position = position;
    }

    void move(final RandomMovingStrategy movingStrategy) {
        if (movingStrategy.movable()) {
            position += POINT_VARIATION_OF_MOVING;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Car car = (Car) o;
        return position == car.position &&
               Objects.equals(name, car.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, position);
    }
}
