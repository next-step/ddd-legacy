package racingcar;

public class Car {

    public Car(String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException();
        }
    }
}
