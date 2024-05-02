package racingcar;

public class Car {

    private final String name;
    private int nameLengthLTECondition;
    private int position;

    public Car(String name) {
        this(name, 5);
    }

    public Car(final String name, int nameLengthCondition) {
        if (name.length() > this.nameLengthLTECondition) {
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.nameLengthLTECondition = nameLengthCondition;
    }

    public String name() {
        return this.name;
    }


    public void move(final MovingStrategy condition) {
        if (condition.movable()) {
            position++;
        }
    }

    public int position() {
        return this.position;
    }
}