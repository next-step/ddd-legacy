package racingcar;

import java.util.Objects;

public class Car {

    private static final int MAX_NAME_SIZE = 5;

    private final String name;
    private int position = 0;
    private MovingStrategy movingStrategy;

    public Car(final String name,
               final MovingStrategy movingStrategy) {
        validateName(name);
        this.name = name;
        this.movingStrategy = movingStrategy;
    }

    private void validateName(String name) {
        if (Objects.isNull(name) || name.isBlank()) {
            throw new IllegalArgumentException("자동차 이름은 비어 있을 수 없습니다");
        }

        if (name.length() > MAX_NAME_SIZE) {
            throw new IllegalArgumentException(String.format("자동차의 이름은 최대 %d 글자를 넘을 수 없습니다.", MAX_NAME_SIZE));
        }
    }

    public void move() {
        if (movingStrategy.canMove()) {
            this.position++;
        }
    }

    public int currentPosition() {
        return this.position;
    }
}
