package racingcar;

public class Car {

    private static final int MAX_NAME_LENGTH = 5;
    private static final int DEFAULT_POSITION = 0;

    private String name;
    private int position;

    public Car(String name) {
        this(name, DEFAULT_POSITION);
    }

    public Car(String name, int position) {
        validateName(name);
        this.name = name;
        this.position = position;
    }

    private void validateName(String name) {
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("자동차 이름은 최대 " + MAX_NAME_LENGTH + " 자 까지 입력가능합니다." );
        }
    }

    public void move(MovingStrategy strategy) {
        if (strategy.movable()) {
            position++;
        }
    }

    public int getPosition() {
        return position;
    }
}
