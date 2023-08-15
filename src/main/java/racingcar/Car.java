package racingcar;

public class Car {

    private final static int CAR_NAME_MAX_LENGTH = 5;
    private final String name;
    private int position;

    public Car(String name) {
        validate(name);
        this.name = name;
        this.position = 0;
    }

    private void validate(String name) {
        if (name.length() >= CAR_NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("자동차 이름은 5글자가 넘을수 없습니다");
        }
    }

    public void move(final MoveCondition condition) {
        if (condition.movable()) {
            move();
        }
    }

    private void move() {
        this.position++;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }
}