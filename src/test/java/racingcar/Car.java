package racingcar;


public class Car {
    private static final int MAXIMUM_NAME_LENGTH = 5;
    private static final int MOVE_CONDITION = 4;

    private final String carName;
    private int position;

    public Car(final String carName) {
        this(carName, 0);
    }

    public Car(final String carName, final int position) {
        if (carName == null || carName.isBlank()) {
            throw new IllegalArgumentException("자동차 이름은 비어 있을 수 없습니다.");
        }
        if (carName.length() > MAXIMUM_NAME_LENGTH) {
            throw new IllegalArgumentException("자동차 이름은 5글자를 넘을 수 없습니다.");
        }
        this.carName = carName;
        this.position = position;
    }

    public void move(final MovingStrategy movingStrategy) {
        if (movingStrategy.movable()) {
            this.position++;
        }
    }

    public int getPosition() {
        return this.position;
    }
}
