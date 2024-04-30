package racingcar;

public class Car {
    private final String name;
    private int position;

    public Car(String name) {
        this(name, 0);
    }

    public Car(String name, int position) {
        if (name.length() > 5) {
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.position = position;
    }

    public void move(int condition) {
        move(() -> condition >= 4);
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
