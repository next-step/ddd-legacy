package racingcar;

class Car {
    private final String name;
    private int position;

    public Car(String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException();
        }

        this.name = name;
    }

    public void move(MoveCondition condition) {
        if (condition.movable()) {
            position++;
        }
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }
}
