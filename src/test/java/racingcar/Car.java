package racingcar;

public class Car {
    private static final int MAXIMUM_NAME_LENGTH = 5;

    private final String name;
    private int position;

    public Car(final String name, final int position) {
        this.validateName(name);
        this.name = name;
        this.position = position;
    }

    public Car(final String name) {
        this(name, 0);
    }

    public void move(final MovingStrategy movingStrategy) {
        if (movingStrategy.movable()) {
            this.position++;
        }
    }

    public int getPosition() {
        return position;
    }

    private void validateName(final String name) {
        if (name == null) throw new IllegalArgumentException("자동차 이름은 비어있을 수 없습니다.");
        if (name.isBlank()) throw new IllegalArgumentException("자동차 이름은 비어있을 수 없습니다.");
        if (name.length() > MAXIMUM_NAME_LENGTH) throw new IllegalArgumentException("자동차 이름은 5글자를 넘을 수 없습니다.");
    }
}
