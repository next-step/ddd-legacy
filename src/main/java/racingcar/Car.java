package racingcar;

import java.util.Random;

class Car {

    private final String name;
    private int position;

    public Car(final String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException("자동차 이름은 5글자 이상을 넘을 수 없습니다.");
        }

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public void move(int condition) {
        if (condition >= 4) {
            position++;
        }
    }

    public void move() {
        final int randomValue = new Random().nextInt(10);
        move(randomValue);
    }

    public void move(final MoveCondition condition) {
        if (condition.movable()) {
            position++;
        }
    }
}