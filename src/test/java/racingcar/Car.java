package racingcar;

import java.util.Objects;

public class Car {
    private final CarName name;
    private MovingStrategy movingStrategy;

    public Car(String name) {
        this.name = CarName.of(name);
        movingStrategy = new RandomMovingStrategy();
    }

    public Car(String name, MovingStrategy movingStrategy) {
        this.name = CarName.of(name);
        this.movingStrategy = Objects.requireNonNull(movingStrategy);
    }

    public String getName() {
        return name.getValue();
    }

    public boolean move() {
        return this.movingStrategy.moveAble();
    }

    public void setMovingStrategy(MovingStrategy movingStrategy) {
        this.movingStrategy = Objects.requireNonNull(movingStrategy);
    }
}
