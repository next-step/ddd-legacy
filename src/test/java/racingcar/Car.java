package racingcar;

public class Car {
    private final String name;
    private int position;

    public Car(final String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.position = 0;
    }

    public void move(final int condition) {
        if (condition >= 4) {
            position++;
        }
    }

    public int getPosition() {
        return position;
    }
}
