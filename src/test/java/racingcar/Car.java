package racingcar;

public class Car {
    public static final int DEFAULT_POSITION = 0;
    public static final int NAME_LENGTH_LIMIT = 5;

    private final String name;
    private int position;

    public Car(final String name) {
        this(name, DEFAULT_POSITION);
    }

    public Car(final String name, final int position) {
        validate(name);
        this.name = name;
        this.position = position;
    }

    private void validate(final String name) {
        if (name.length() > NAME_LENGTH_LIMIT) {
            throw new IllegalArgumentException();
        }
    }

    public void move(final MovingStrategy movingStrategy) {
        if (movingStrategy.isMovable()) {
            position++;
        }
    }

    public int getPosition() {
        return position;
    }
}
