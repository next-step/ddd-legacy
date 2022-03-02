package racingcar;

public class Car {
    private final String name;
    private int position = 0;

    public Car(String name) {
        this.name = name;
    }

    private void validateName(String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException();
        }
    }

    public void move(MovingStrategy strategy) {
        if (strategy.movable()) {
            position += 1;
        }
    }

    public int getPosition() {
        return position;
    }
}
