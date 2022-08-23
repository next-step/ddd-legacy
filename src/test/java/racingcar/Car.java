package racingcar;

public class Car {
    private static final int MAXIMUM_NAME_LENGTH = 5;

    private final String name;
    private int position;

    public Car(String name) {
        this(name, 0);
    }

    public Car(String name, int position) {
        validateName(name);
        this.name = name;
        this.position = position;
    }

    public void move(final MovingStrategy movingStrategy) {
        if (movingStrategy.movable()) {
            this.position++;
        }
    }

    public int position() {
        return this.position;
    }

    private void validateName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("자동차 이름은 비어있을 수 없습니다.");
        }

        if (name.isBlank()) {
            throw new IllegalArgumentException("자동차 이름은 비어있을 수 없습니다.");
        }

        if (name.length() > MAXIMUM_NAME_LENGTH) {
            throw new IllegalArgumentException("자동차의 이름이 5글자가 넘습니다.");
        }
    }

}
