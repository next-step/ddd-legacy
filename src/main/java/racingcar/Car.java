package racingcar;

import java.util.Random;

public class Car {

    private final String name;
    private int position;

    public Car(final String name, int position) {
        if (name.length() > 5) {
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void move(final int condition) {
        if (condition >= 4) {
            position++;
        }
    }
}
