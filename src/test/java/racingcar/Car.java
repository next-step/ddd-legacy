package racingcar;

public class Car {
    private final String name;
    private int position;

    public Car(String name) {
        validate(name);
        this.name = name;
    }

    private void validate(String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException("자동차의 이름은 5글자를 넘길 수 없습니다.");
        }
    }

    public Car(String name, int position) {
        this.name = name;
        this.position = position;
    }

    public void move(MovingStrategy movingStrategy) {
        if (movingStrategy.movable()) {
            position++;
        }
    }

    public int getPosition() {
        return position;
    }
}
