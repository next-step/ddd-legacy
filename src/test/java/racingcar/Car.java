package racingcar;

public class Car {
    private String name;

    public Car(String name) {
        if(name.length() >= 5) {
            throw new IllegalArgumentException("자동차 이름은 5글자를 넘을 수 없다.");
        }
        this.name = name;
    }


    public boolean isMoving(MovementStrategy ms) {
        return ms.move();
    }
}
