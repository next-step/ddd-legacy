package racingcar;

public class Car {

    private final String name;
    private int position = 0;

    public Car(final String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException();
        }
        this.name = name;
    }

}
