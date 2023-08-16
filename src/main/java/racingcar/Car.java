package racingcar;

public class Car {
    public static final int MAX_NAME_LENGTH = 5;

    private final String name;
    private int position;

    public Car(final String name) {
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException();
        }
        this.name = name;
    }

    public void move(MoveStrategy moveStrategy) {
        if (!moveStrategy.movable()) {
            return;
        }
        position++;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }
}
