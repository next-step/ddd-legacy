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

    public int position() {
        return this.position;
    }

    public void move(int value) {
        if (value >= 4) {
            this.position++;
        }
    }
}
