package racingcar;

public class Car {
    private static final int MAX_NAME_LENGTH = 5;

    private int position;
    private String name;

    public Car(String name, int position) {
        if (isValidCarName(name) && isValidCarPosition(position)) {
            this.name = name;
            this.position = position;
        }
    }
    private boolean isValidCarName(String name) {
        if (name == null || name.length() <= 0) {
            throw new IllegalArgumentException("자동차 이름은 비어있을 수 없습니다.");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("자동차 이름은 5 글자를 넘을 수 없다.");
        }
        return true;
    }

    private boolean isValidCarPosition(int position) {
        if (position < 0) {
            throw new IllegalArgumentException("자동차 위치는 0보다 작을 수 없다.");
        }
        return true;
    }

    public boolean isMoveForwardStatus(int beforePosition) {
        isValidCarPosition(beforePosition);
        return (this.position > beforePosition);
    }

    public boolean isStopStatus(int beforePosition) {
        isValidCarPosition(beforePosition);
        return (this.position == beforePosition);
    }

    public Car move(MovingStrategy strategy) {
        if (strategy.isMovable(this.position)) {
            position++;
        }
        return this;
    }
}