package racingcar;

public class Car {

    private String carName;

    public Car(String carName) {
        if (carName == null) {
            return;
        }

        if (carName.length() > 5) {
            throw new IllegalArgumentException();
        }

        this.carName = carName;
    }
}
