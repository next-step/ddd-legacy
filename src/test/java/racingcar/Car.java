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

    public void move(MovingStrategy condition) {
        if (condition.movable()) {
            position++;
        }
    }

    public int position() {
        return position;
    }
}
