package racingcar;

public class Car {
    private final String name;
    private int position;

    public Car(String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException("이름은 5자리를 넘을 수 없다.");
        }
        this.name = name;
    }

    public void move(MoveCondition moveCondition) {
        if (moveCondition.movable()) {
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
