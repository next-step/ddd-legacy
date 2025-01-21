package racingcar;

import java.util.Objects;

public class Car {

    private CarName carName;
    private MoveStrategy moveStrategy;
    private int position;

    public Car(CarName carName, MoveStrategy moveStrategy, int position) {
        this.carName = carName;
        this.moveStrategy = moveStrategy;
        this.position = position;
    }

    public Car(CarName carName, MoveStrategy moveStrategy) {
        this(carName, moveStrategy, 0);
    }

    public void race(int number) {
        if (moveStrategy.movable(number)) {
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
        return position == car.position && Objects.equals(carName, car.carName) && Objects.equals(
                moveStrategy, car.moveStrategy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(carName, moveStrategy, position);
    }
}
