package racingcar;

import java.util.Random;

public class Car {
    private static final int CAR_NAME_MAX_LENGTH = 5;

    private final String name;
    private int position;

    public Car(String name) {
        if (name.length() > CAR_NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("자동차 이름은 5자 이하만 가능합니다.");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void move() {
        final int condition = new Random().nextInt(10);
        if (condition >= 4) {
            position++;
        }
    }

    public void move(int condition) {
        if (condition >= 4) {
            position++;
        }
    }

    public void move(final MoveCondition condition) {
        if (condition.isMovable()) {
            position++;
        }
    }

    public int getPosition() {
        return position;
    }
}
