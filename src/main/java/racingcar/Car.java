package racingcar;

public class Car {

    private final String name;
    private int position;

    public Car(final String name) {
        validateName(name);
        this.name = name;
    }

    private void validateName(final String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException("자동차의 이름을 5글자를 넘을 수 없습니다.");
        }
    }

    public void move(final MoveCondition condition) {
        if (condition.movable()) {
            position++;
        }
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

}
