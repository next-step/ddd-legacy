package racingcar;

public class Car {

    private final String name;
    private Integer position;

    public Car(String name) {
        this(name, 0);
    }

    public Car(String name, Integer position) {
        if (name == null || name.isBlank() || name.length() > 5) {
            throw new IllegalArgumentException("자동차 이름은 5 글자를 넘을 수 없다.");
        }

        this.name = name;
        this.position = position;
    }

    public void move(MovingStrategy strategy) {
        if (strategy.movable()) {
            position++;
        }
    }
}
