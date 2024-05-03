package racingcar;

public class Car {

    private final String name;
    private final int nameLengthLTECondition;
    private int position;

    public Car(String name) {
        this(name, 5);
    }

    public Car(final String name, int nameLengthCondition) {
        this.nameLengthLTECondition = nameLengthCondition;

        if (name.length() >= this.nameLengthLTECondition) {
            throw new IllegalArgumentException("Name length should be under condition, given " + name.length());
        }
        this.name = name;
    }

    public String name() {
        return this.name;
    }


    public void move(final MovingStrategy condition, int moveableCarLimitScore) {
        if (condition.movable(moveableCarLimitScore)) {
            position++;
        }
    }

    public void move(final MovingStrategy condition) {
        this.move(condition, 4);
    }

    public int position() {
        return this.position;
    }
}