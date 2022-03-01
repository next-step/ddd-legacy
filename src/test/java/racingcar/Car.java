package racingcar;

public class Car {

    private final String name;
    private int position;

    public Car(final String name) {
        this(name, 0);
    }

    public Car(final String name, final int position) {
        this.validate(name);

        this.name = name;
        this.position = position;
    }

    public int getPosition() {
        return this.position;
    }

    public void move(final int number) {
        if (number >= 4) {
            position++;
        }
    }

    public void move(final MovingStrategy movingStrategy) {
        if (movingStrategy.moveable()) {
            position++;
        }
    }

    private void validate(final String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException("자동차 이름은 5 글자를 넘을 수 없다.");
        }
    }
}
