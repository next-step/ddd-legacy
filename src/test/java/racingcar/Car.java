package racingcar;

public class Car {

    private final String name;
    private int position;

    public Car(final String name) {
        this(name, 0);
    }

    private void validate(final String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException("자동차의 이름은 5글자를 넘길 수 없습니다.");
        }
    }

    public Car(final String name, final int position) {
        this.name = name;
        this.position = position;
        validate(this.name);
    }

//    public void move(final int number) {
//        if (number >= 4) {
//            position++;
//        }
//    }

    public void move(final MovingStrategy movingStrategy) {
        if (movingStrategy.movable()) {
            position++;
        }
    }

    public int getPosition() {
        return position;
    }
}
