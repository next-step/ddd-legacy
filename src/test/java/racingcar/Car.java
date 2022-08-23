package racingcar;

public class Car {
    private static final int MAX_NAME_LENGTH = 5;
    private int position;

    public Car(final String name) {
        this(name, 0);
    }

    public Car(final String name, final int initPosition) {
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("이름은 5자를 넘을 수 없습니다.");
        }
        this.position = initPosition;
    }

    public int getPosition() {
        return position;
    }

    public void move(int condition) {
        if (condition >= 4) {
            position++;
        }
    }
}
