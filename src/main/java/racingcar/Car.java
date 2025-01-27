package racingcar;

/**
 * 차 이름 중도 변경, 이동 전략 중도 변경 등을 고려하여 final 키워드를 사용하지 않음
 */
public class Car {

    private final CarName name;
    private CarPosition position;
    private final MoveStrategy moveStrategy;

    public Car(String name, int initPosition, MoveStrategy moveStrategy) {
        this.name = new CarName(name);
        this.position = new CarPosition(initPosition);
        this.moveStrategy = moveStrategy;
    }

    public void move(int input) {
        this.position = moveStrategy.getNextPosition(position, input);
    }

    public int getPosition() {
        return position.getCurrentPosition();
    }
}
