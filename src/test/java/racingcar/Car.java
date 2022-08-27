package racingcar;

public class Car {
    private final CarName name;

    public Car(String name) {
        this.name = CarName.of(name);
    }

    public String getName() {
        return name.getValue();
    }
}
