package racingcar;

public class Car {
    public static final int MAX_CAR_NAME_LENGTH = 5;
    public static final int MIN_MOVE_COUNT = 4;
    private final String name;
    private int position;

    public Car(final String name) {
        if (name.length() > MAX_CAR_NAME_LENGTH) {
            throw new IllegalArgumentException("자동차 이름의 길이는 5글자를 초과할 수 없습니다.");
        }
        this.name = name;
    }

    public void move(final MovingStrategy movingStrategy) {
        if (movingStrategy.movable()) {
            position++;
        }
    }

    public int getPosition() {
        return position;
    }

}
