package racingcar.domain;

public class Car {
    private String name;
    private int position;

    public Car() {
        name = "";
        position = 0;
    }

    public Car(String name) {
        handleNameLength(name);
        this.name = name;
    }

    private void handleNameLength(String name) {
        if (5 < name.length()) {
            throw new IllegalArgumentException("자동차 이름은 5 글자를 넘을 수 없습니다.");
        }
    }

    public boolean movable(MoveStrategy strategy, int value) {
        if (strategy.isMovable(value)) {
            position++;
            return true;
        }
        return false;
    }
}