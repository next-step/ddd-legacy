package racingcar;

public class Car {
    private final String name;
    private int position = 0;

    public Car(String name) {
        validateName(name);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public void move(MovingStrategy movingStrategy) {
        if (movingStrategy.isMovable()) {
            position++;
        }
    }

    private void validateName(String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException("자동차 이름은 5 글자를 넘을 수 없습니다.");
        }
    }
}
