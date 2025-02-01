package mission.step0.racingcar;

public class Car {
    private final String name;
    private int position;


    public Car(final String name) {
        if(name.length() > 5) {
            throw new IllegalArgumentException("자동차 이름은 5글자를 초과할 수 없습니다.");
        }

        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void move(final int condition) {
        move(() -> condition >= 4);
    }

    public void move(final MovingStrategy movingStrategy) {
        if(movingStrategy.movable()) {
            position++;
        }
    }
}
