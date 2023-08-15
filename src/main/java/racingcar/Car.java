package racingcar;

import java.util.Random;

class Car {
    private final String name;
    private int position;

    public Car(String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException();
        }

        this.name = name;
    }

    public void move() {
        final int condition = new Random().nextInt(10);
        if (condition >= 4) {
            position++;
        }
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }
}
