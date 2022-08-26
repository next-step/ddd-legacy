package racingcar;

public class Car {
    private String name;
    private int position;

    public Car(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name은 null 일 수 없다");
        }

        if (name.length() > 5) {
            throw new IllegalArgumentException("name 은 5글자를 넘을 수 없다");
        }

        this.name = name;
    }

    public void move(MovingStrategy strategy) {
        if (strategy.movable()) {
            this.position++;
        }
    }

    public int getPosition() {
        return position;
    }
}
