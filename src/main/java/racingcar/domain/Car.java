package racingcar.domain;

public class Car {

    private final String name;

    private int position;

    public Car(String name) {
        this(name, 0);
    }

    public Car(String name, int position) {
        this.name = name;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

}
