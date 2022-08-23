package racingcar;

public class Car {

    private static final int MAXIMUM_NAME_LENGTH = 5;
    private static final int INITIAL_POSITION = 0;

    private final String name;
    private int position;

    public Car(final String name) {
        this(name, INITIAL_POSITION);
    }

    public Car(final String name, final int position) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("자동차의 이름은 비어 있을 수 없습니다.");
        }
        if (name.length() > MAXIMUM_NAME_LENGTH) {
            throw new IllegalArgumentException("자동차의 이름은 5글자를 넘을 수 없습니다.");
        }
        this.name = name;
        this.position = position;
    }

    public void move(final MovingStrategy movingStrategy) {
        if (movingStrategy.movable()) {
            this.position++;
        }
    }

    public int getPosition() {
        return this.position;
    }
}
