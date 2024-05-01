package racingcar.domain;

import utils.*;

public class Car {

    private final String name;

    private int position;

    private static final int MAXIMUM_NAME_LENGTH = 5;

    public Car(String name) {
        this(name, 0);
    }

    private Car(String name, int position) {
        if (StringUtils.isBlankWhenTrim(name)) {
            throw new IllegalArgumentException("자동차의 이름은 비어있을 수 없습니다.");
        }

        if (name.length() > MAXIMUM_NAME_LENGTH) {
            throw new IllegalArgumentException("자동차의 이름은 5자를 넘을 수 없습니다.");
        }

        this.name = name;
        this.position = position;
    }

    public void move(final MovingStrategy movingStrategy) {
        if (movingStrategy.movable()) {
            this.position ++;
        }
    }

    public int getPosition() {
        return position;
    }

}
