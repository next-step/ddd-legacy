package racingcar;

import java.util.Random;

public class Car {
    private final String name;
    private int position;

    public Car(final String name) {
        this(name, 0);
    }

    public Car(final String name, final int position) {
        validate(name);
        this.name = name;
        this.position = position;
    }

    private void validate(final String name) {
        //validate 빼줄 수 있음
        if (name.length() > 5) {
            throw new IllegalArgumentException("5글자를 넘길 수 없습니다.");
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
