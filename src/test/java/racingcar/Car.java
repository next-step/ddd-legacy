package racingcar;

import racingcar.strategy.MovingStrategy;

import java.util.Objects;

public class Car {

    private static final int MAXIMUM_NAME_LENGTH = 5;
    private static final int DEFAULT_POSITION = 0;
    private static final int MOVING_CONDITION = 4;
    private final String name;
    private int position;

    public Car(final String name) {
        this(name, DEFAULT_POSITION);
    }

    public Car(final String name, final int position) {
        validate(name, position);
        this.name = name;
        this.position = position;
    }

    private void validate(String name, int position) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("자동차의 이름은 비어 있을 수 없습니다.");
        }
        if (name.length() > MAXIMUM_NAME_LENGTH) {
            throw new IllegalArgumentException("자동차의 이름은 5글자를 넘길 수 없습니다.");
        }
        if (position < DEFAULT_POSITION) {
            throw new IllegalArgumentException("자동차의 위치는 음수일 수 없습니다.");
        }
    }

    public void move(final MovingStrategy strategy) {
        if (strategy.movable()) {
            position++;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Car car = (Car) o;
        return position == car.position && Objects.equals(name, car.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, position);
    }
}
