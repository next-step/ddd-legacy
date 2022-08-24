package racingcar;


import java.util.Objects;

public class Car {

    public static final int MAXIMUM_NAME_LENGTH = 5;
    public static final int MOVING_CONDITION = 4;


    private final String name;
    private int position;

    public Car(String name) {
        this(name, 0);
    }

    public Car(String name, int i) {
        if(Objects.isNull(name) || name.isBlank()){
            throw new IllegalArgumentException("name cannot be null");
        }

        if (name.length() > MAXIMUM_NAME_LENGTH) {
            throw new IllegalArgumentException("name length should be longer than 5");
        }
        this.name = name;
        this.position = i;
    }

    public void move(int condition) {
        if (condition >= MOVING_CONDITION) {
            position++;
        }
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
