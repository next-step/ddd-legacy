package car.domain;

import car.strategy.MoveStrategy;
import java.util.Objects;

public class Car {
    private final CarName carName;
    private final Position position;

    private Car(final CarName carName, final Position position) {
        this.carName = carName;
        this.position = position;
    }

    public static Car of(final String name, final int moveNumber) {
        return new Car(CarName.from(name), Position.from(moveNumber));
    }

    public Car move(final int moveNumber ,final MoveStrategy moveStrategy) {
        return new Car(this.carName, position.move(moveStrategy.movable(moveNumber)));
    }

    public String getCarName() {
        return this.carName.getName();
    }

    public int getMoveNumber() {
        return this.position.getNumber();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return Objects.equals(carName, car.carName) && Objects.equals(position, car.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(carName, position);
    }

    @Override
    public String toString() {
        return "Car{" +
                "carName=" + carName +
                ", moveNumber=" + position +
                '}';
    }

}
