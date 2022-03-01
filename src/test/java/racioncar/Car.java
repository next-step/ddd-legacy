package racioncar;

public class Car {

    private final String name;
    private int position;

    public Car(String name) {
        this(name, 0);
    }

    public Car(String name, int position) {
        if (name.length() > 5) {
            throw new IllegalArgumentException("이름은 5자가 넘을 수 없습니다.");
        }
        this.name = name;
        this.position = position;
    }

    public void move(MoveStrategy moveStrategy) {
        if (moveStrategy.movable()) {
            position++;
        }
    }

    public int getPosition() {
        return position;
    }
}
