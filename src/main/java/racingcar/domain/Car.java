package racingcar.domain;

import racingcar.strategy.MoveStrategy;

public class Car {
    private static final int MAX_OF_NAME = 5;
    private static final String NAME_BLANK_EXCEPTION_MESSAGE = "자동차 이름은 비어있을수 없습니다.";
    private static final String NAME_SIZE_EXCEPTION_MESSAGE = "자동차 이름은 %d 글자를 넘을 수 없습니다.";

    private final String name;
    private int distance;

    public Car(String name) {
        validateName(name);
        this.name = name;
        this.distance = 0;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(NAME_BLANK_EXCEPTION_MESSAGE);
        }
        if (name.length() > MAX_OF_NAME) {
            throw new IllegalArgumentException(String.format(NAME_SIZE_EXCEPTION_MESSAGE, MAX_OF_NAME));
        }
    }

    public void move(MoveStrategy moveStrategy) {
        if (moveStrategy.canMove()) {
            distance++;
        }
    }

    public int getDistance() {
        return distance;
    }
}
