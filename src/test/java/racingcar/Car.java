package racingcar;

public class Car {

    private static final int CAR_NAME_LENGTH = 5;

    private String name;
    private Position position = new Position(0);

    public Car(String name) {
        validate(name);
    }

    private void validate(String name) {
        if (name == null) {
            throw new IllegalStateException();
        }
        if (name.length() > CAR_NAME_LENGTH) {
            throw new IllegalStateException();
        }
    }

    public void move(MovingStrategy movingStrategy) {
        if (movingStrategy.canMove()) {
            this.position = position.up();
        }
    }
}
