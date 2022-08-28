package racingcar;

public class Car {
    private static final int MAX_NAME_LENGTH = 5;
    private static final int MIN_MOVABLE_THRESHOLD = 4;
    private final String name;
    private int position;

    public Car(final String name) {
        this(name, 0);
    }

    public Car(final String name, final int initPosition) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 비어 있을 수 없습니다.");
        }

        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("이름은 5자를 넘을 수 없습니다.");
        }
        this.name = name;
        this.position = initPosition;
    }

    public int getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public void move(MovingStrategy strategy) {
        if (strategy.canMove()) {
            position++;
        }
    }
}
