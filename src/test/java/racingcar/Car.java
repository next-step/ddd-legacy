package racingcar;

public class Car {
    private final static int MOVE_CONDITION = 4;
    private final static int NAME_LENGTH_LIMIT = 5;
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
        if (name.length() > NAME_LENGTH_LIMIT) {
            throw new IllegalArgumentException("자동차 이름은 5글자를 넘을 수 없습니다.");
        }
    }

    public void move(int number) {
        if (number >= MOVE_CONDITION) {
            position++;
        }
    }

    public void move(MovingStrategy strategy) {
        if (strategy.movable()) {
            position ++;
        }
    }

    public int getPosition() {
        return this.position;
    }
}
