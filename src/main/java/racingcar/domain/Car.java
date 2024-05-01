package racingcar.domain;

import racingcar.strategy.MoveStrategy;

public class Car {
    private static final int MIN_POSITION = 0;
    
    private final Name name;
    private int position;

    public Car(String name) {
        this(name, MIN_POSITION);
    }

    private Car(String name, int position) {
        this.name = new Name(name);
        this.position = position;
    }

    public void move(MoveStrategy moveStrategy) {
        if (moveStrategy.isMovable()) {
            position++;
        }
    }

    public int getPosition() {
        return position;
    }
}
