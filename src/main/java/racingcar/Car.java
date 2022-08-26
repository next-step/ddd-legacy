package racingcar;

public class Car {
    private static final int MAX_NAME_LENGTH = 5;
    private static final String MAX_NAME_LENGTH_MESSAGE = "자동차 이름은 5글자보다 작거나 같아야 합니다.";
    private static final String EMPTY_NAME_MESSAGE = "자동차 이름은 비어있을 수 없습니다.";

    private String name;
    private int position;

    public Car(String name) {
        this(name, 0);
    }

    public Car(String name, int position) {
        if (isEmpty(name)) {
            throw new IllegalArgumentException(EMPTY_NAME_MESSAGE);
        }

        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException(MAX_NAME_LENGTH_MESSAGE);
        }

        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public void move(MovingStrategy strategy) {
        if (strategy.movable()) {
            position++;
        }
    }

    private boolean isEmpty(String name) {
        return name == null || name.isBlank();
    }
}
