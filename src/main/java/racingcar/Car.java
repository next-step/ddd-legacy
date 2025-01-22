package racingcar;

public class Car {
    public static final int MAX_CAR_NAME_LENGTH = 5;
    public static final int MIN_MOVE_COUNT = 4;
    private final String name;
    private int position;

    public Car(final String name) {
        if (name.length() > MAX_CAR_NAME_LENGTH) {
            throw new IllegalArgumentException();
        }
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void move(final int condition) {
            move(() -> condition >= MIN_MOVE_COUNT);
    }

    public void move(final MovingStrategy movingStrategy) {
        if (movingStrategy.movable()) {
            position++;
        }
    }
}
