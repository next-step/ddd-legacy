package racingcar;

import java.util.Random;

public class Car {
    private static final int MAX_NAME_LENGTH = 5;
    private final String name;
    private int position;

    public Car(String name) {
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Car name cannot be longer than 5 characters");
        }
        this.name = name;
        this.position = 0;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public void move() {
        Random random = new Random();
        int randomValue = random.nextInt(10);
        if (randomValue >= 4) {
            position++;
        }
    }
}
