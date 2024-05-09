package racingcar;

class Car {

    private String name;
    private int position;

    public void move(final int condition) {
        move(() -> condition >= 4);
    }

    public void move(final MovingStrategy condition) {
        if (condition.movable()) {
            position++;
        }
    }

    public int position() {
        return position;
    }

    public Car(final String name) {
        this(name, 0);
    }

    public Car(final String name, final int position) {
        if (name.length() > 5) {
            throw new IllegalArgumentException();
        }

        this.name = name;
        this.position = position;
    }
}
