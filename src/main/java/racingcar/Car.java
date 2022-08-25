package racingcar;

public class Car {
    private static final int MAXIMUM_NAME_LENGTH = 5;

    private String name;
    private int position;

    public Car(String name) {
        this(name, 0);
    }

    public Car(String name, int position) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("자동차의 이름은 비어있을 수 없습니다.");
        }

        if (name.length() > MAXIMUM_NAME_LENGTH) {
            throw new IllegalArgumentException("자동차의 이름은 " + MAXIMUM_NAME_LENGTH + "글자 이하여야 합니다.");
        }
        
        this.name = name;
        this.position = position;
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
