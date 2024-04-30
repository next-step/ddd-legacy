package racingcar;

public class Car {
    private final String name;
    private int position;

    public Car(String name, int position) {
        if (name.length() > 5) {
            throw new IllegalArgumentException("자동차 이름은 5글자를 넘을 수 없습니다.");
        }
        this.name = name;
        this.position = position;
    }

    public Car(String name) {
        this(name, 0);
    }

    public void move(int condition) {
        if (condition >= 4) {
            this.position++;
        }
    }

    public int position() {
        return this.position;
    }
}
