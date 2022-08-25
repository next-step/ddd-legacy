package racingcar;

public class Car{

    private String name;
    private int position;

    private Car(String name) {
        this.name = name;
        this.position = 0;
    }

    public static Car of(String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException("자동차 이름은 5글자를 넘길 수 없습니다.");
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
