package racingcar;

public class Car {

    private final Engine engine;
    private int distance;

    public Car(String name, Engine engine) {
        if (name.length() > 5) {
            throw new IllegalArgumentException();
        }

        this.engine = engine;
        distance = 0;
    }

    public void move() {
        if (engine.generatePower() >= 4) {
            distance++;
        }
    }

    public int distance() {
        return distance;
    }
}
