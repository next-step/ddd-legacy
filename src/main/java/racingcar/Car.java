package racingcar;

public class Car {

    private static final int LIMIT_CAR_NAME_LENGTH = 5;

    private final String name;
    private Integer position;

    public Car(String name) {
        this(name, 0);
    }

    public Car(String name, Integer position) {
        checkValidCarName(name);

        this.name = name;
        this.position = position;
    }

    private static void checkValidCarName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("자동차 이름은 비어 있을수 없습니다.");
        }

        if (name.length() > LIMIT_CAR_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("자동차 이름은 %d 글자를 넘을 수 없다.", LIMIT_CAR_NAME_LENGTH));
        }
    }

    public void move(MovingStrategy strategy) {
        if (strategy.movable()) {
            position++;
        }
    }
}
