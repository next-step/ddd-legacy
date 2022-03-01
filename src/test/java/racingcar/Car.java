package racingcar;

import java.util.Random;

public class Car {
    private final String name;
    private int position;

    public Car(final String name) {
        validateName(name);
        this.name = name;
    }
    public Car(final String name, final int position) {
        this(name);
        this.position = position;
    }

    private void validateName(String name) {
        if(name.length() >  5) {
            throw new IllegalArgumentException("자동차의 이름은 5글자를 넘길 수 없습니다.");
        }
    }

    public void move(final MovingStrategy movingStrategy) {
        if(movingStrategy.movable()) {
            position++;
        }
    }

    public int getPosition() {
        return this.position;
    }
}
