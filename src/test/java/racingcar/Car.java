package racingcar;

public class Car {

    String name;
    int position;

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

    public void move(final MovingStrategy condition) {
        if (condition.movable()) {
            position++;
        }
    }

    public int getPosition() {
        return position;
    }
}