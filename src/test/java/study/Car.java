package study;

import study.constant.Constant;

public class Car {

    private final String name;
    private int position;

    public Car(final String name) {
        if (name.length() > Constant.CAR_NAME_LENGTH_LIMIT) {
            throw new IllegalArgumentException();
        }

        this.name = name;
    }

    public void move(int input) {
        if (input >= Constant.CAR_MOVABLE_CONDITION_MIN) {
            position++;
        }
    }

    public void move(CarMovingStrategy strategy) {
        if (strategy.movable()) {
            position++;
        }
    }

    public int getPosition() {
        return this.position;
    }

}
