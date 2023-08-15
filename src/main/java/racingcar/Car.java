package racingcar;

public class Car {
    private final String name;
    private int position;

    public Car(String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException();
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void move(final MoveCondition condition) {
        if (condition.movable()) {
            position++;
        }

    }

    public int getPosition() {
        return position;
    }
}
