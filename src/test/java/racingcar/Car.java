package racingcar;

public class Car {

    private static final int MOVE_CONDITION = 4;

    private final CarName name;
    private CarPosition position;

    public Car(String name, int position) {
        this(new CarName(name), new CarPosition(position));
    }

    public Car(CarName name, CarPosition position) {
        this.name = name;
        this.position = position;
    }

    public void move(MoveStrategy strategy) {
        if (strategy.isMovable()) {
            this.position = new CarPosition(this.position.getValue() + 1);
        }
    }

    public int getPositionValue() {
        return position.getValue();
    }
}
