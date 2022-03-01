package racingcar;

public class Car {

    private final String name;
    private int position;

    public Car(String name) {
        this(name, 0);
    }

    public Car(String name, int position) {
        validate(name);
        this.name = name;
        this.position = position;
    }

    private void validate(String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException("자동차의 이름은 5글자를 넘길 수 없습니다.");
        }
    }

    public int position() {
        return position;
    }

    public void move(MovingStrategy randomMovingStrategy) {
        if (randomMovingStrategy.movable()) {
            position++;
        }
    }

}
