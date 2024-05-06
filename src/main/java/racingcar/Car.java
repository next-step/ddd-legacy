package racingcar;

public class Car {
    private String name;
    private int position;

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public Car() {
        this("", 0);
    }

    public Car(String name) {
        this(name, 0);
    }

    public Car(String name, int position) {
        if (name.length() > 5) throw new IllegalArgumentException("자동차의 이름은 5글자를 넘을 수 없습니다.");
        this.name = name;
        this.position = position;
    }

    public void move(MovingCondition movingCondition, int number) {
        boolean movePossible = movingCondition.isMovePossible(number);
        if (movePossible) {
            position++;
        }
    }
}
