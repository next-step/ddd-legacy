package racingcar;

import java.util.Random;

class Car {

    private String name;
    private int position;

    public Car(String name) {
        if (name.length() > 5) {
            throw new IllegalArgumentException("자동차의 이름은 5글자를 넘을 수 없습니다.");
        }
        this.name = name;
    }

    public void move() {
        move(new Random().nextInt(10));
    }

    public void move(int condition) {
        if (condition >= 4) {
            position++;
        }
    }

    public void move(MoveCondition condition) {
        if (condition.isMovable()) {
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