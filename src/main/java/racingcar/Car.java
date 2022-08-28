package racingcar;

import racingcar.moving_strategy.MovingStrategy;

public class Car {

    public static final int MAX_NAME_LENGTH = 5;

    private final String name;

    private int position;

    public Car(final String name) {
        this(name, 0);
    }

    public Car(String name, int position) {
        this.validateName(name);
        this.validatePosition(position);

        this.name = name;
        this.position = position;
    }

    public void move(final MovingStrategy movingStrategy) {
        if (movingStrategy.movable()) {
            this.position++;
        }
    }

    private void validateName(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("자동차의 이름은 null일 수 없습니다");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("자동차의 이름은 비어 있을 수 없습니다");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("자동차의 이름은 5글자를 넘을 수 없습니다");
        }
    }

    private void validatePosition(int position) throws IllegalArgumentException {
        if (position < 0) {
            throw new IllegalArgumentException("자동차의 위치는 음수일 수 없습니다");
        }
    }

    public int getPosition() {
        return this.position;
    }
}
