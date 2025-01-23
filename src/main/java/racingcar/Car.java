package racingcar;

import java.util.Random;

public class Car {

    private final String name;
    private int position;

    public Car(final String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException();
        }
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void move() {
        final var condition = new Random().nextInt(10);
        move(condition);
    }

    public void move(final int condition) {
        move(() -> condition >= 4);
    }

    public void move(final MovingStrategy movingStrategy) {
        if (movingStrategy.movable()) {
            position++;
        }
    }
}
