package racingcar;

public class Car {
    private final String name;
    private int position;

    public Car(String name) {
        this(name, 0);
    }

    public Car(String name, int position) {
        if (name.length() > 5) {
            throw new IllegalArgumentException("자동차의 네이밍은 다섯글자를 초과할 수 없습니다.");
        }
        this.name = name;
        this.position = position;
    }

    public void move(MovingStrategy condition, int number) {
        if (condition.movable(number)) {
            this.position++;
        }
    }

    public String name() {
        return name;
    }

    public int position() {
        return position;
    }

}
