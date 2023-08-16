package racingcar;

public class Car {
    private final CarName name;
    private int position;

    public Car(String name) {

        this.name = new CarName(name);
    }

    public void move(MoveStrategy moveStrategy) {
        if (moveStrategy.isMovable()) {
            this.position++;
        }
    }

    public int getPosition() {
        return position;
    }

    public String getName() {
        return name.getValue();
    }

}
