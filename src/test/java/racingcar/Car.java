package racingcar;

public class Car{

    private static final int MAXIMUM_NAME_LENGTH = 5;
    private static final int INITIAL_POSITION = 0;

    private String name;
    private int position;

    private Car(String name) {
        this.name = name;
        this.position = INITIAL_POSITION;
    }

    public static Car of(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("자동차의 이름은 비어 있을 수 없습니다.");
        }
        if (name.length() > MAXIMUM_NAME_LENGTH) {
            throw new IllegalArgumentException("자동차의 이름은 5글자를 넘을 수 없습니다.");
        }
        return new Car(name);
    }

    public void move(MovingStrategy movingStrategy) {
        if (movingStrategy.movable()) {
            position++;
        }
    }

    public int getPosition() {
        return position;
    }
}
