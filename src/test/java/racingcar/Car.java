package racingcar;

import java.util.Random;

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

    public boolean movable() {
        Random random = new Random();
        var num = random.nextInt(10);
        return num >= 4;
    }
}
