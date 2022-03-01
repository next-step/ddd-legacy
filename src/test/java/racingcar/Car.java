package racingcar;

public class Car {

    private static final int DEFAULT_POSITION = 0;
    private static final int NAME_LENGTH = 5;

    private final String name;
    private int position;

    public Car(String name) {
        this(name, DEFAULT_POSITION);
    }

    public Car(final String name, final int position) {
        validate(name);

        this.name = name;
        this.position = position;
    }

    private void validate(final String name) {
        if (name.length() > NAME_LENGTH) {
            throw new IllegalArgumentException("자동차의 이름은 " + NAME_LENGTH + " 글자를 넘어갈 수 없습니다.");
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
