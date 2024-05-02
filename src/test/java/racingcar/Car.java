package racingcar;

public class Car {
    private final CarName carName;
    private long position = 0L;

    public Car(String carName) {
        this.carName = new CarName(carName);
    }

    public long move(CarMovingStrategy carMovingStrategy) {
        if (carMovingStrategy.isMovable()) {
            position++;
        }
        return position;
    }

}
