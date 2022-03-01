package racingcar;

public class Car {

    private final String name;
    private int position;

    public Car(String name) {
        this(name, 0);
    }

    public Car(final String name, final int position) {
        validate(name);

        this.name = name;
        this.position = position;
    }

    private void validate(final String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException("자동차의 이름은 5 글자를 넘어갈 수 없습니다.");
        }
    }

    public void move(final MovingStrategy movingStrategy) {
        if (movingStrategy.movable()) {
            position++;
        }
    }

    public int getPosition() {
        return this.position;
    }
}
