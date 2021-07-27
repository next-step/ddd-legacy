package study;

public class Car {
    private final String name; // FIXME: 현재 참조하지 않음
    private int position;

    public Car(final String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException("자동차 이름은 5 글자를 넘을 수 없음");
        }
        this.name = name;
    }

    public void move(final int condition) {
        if (condition >= 4) {
            position++;
        }
    }

    public void move(final MovingStrategy movingStrategy) {
        if (movingStrategy.movable()) {
            position++;
        }
    }

    public int getPosition() {
        return position;
    }
}
