package racingCar;

import java.util.Random;

public class Car {

    private static final int DEFAULT_POSITION = 0;
    private static final int MINIMUM_MOVE_CONDITION = 4;
    private static final Random random = new Random(10);

    private String name;
    private int position;

    public Car(String name) {
        this(name, DEFAULT_POSITION);
    }

    public Car(String name, int position) {
        validateName(name);
        this.name = name;
        this.position = position;
    }

    public void move() {
        if (random.nextInt() >= MINIMUM_MOVE_CONDITION) {
            this.position++;
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank() || name.length() > 5) {
            throw new IllegalArgumentException();
        }
    }

    public int getPosition() {
        return position;
    }
}
