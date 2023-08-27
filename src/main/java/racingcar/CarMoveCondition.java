package racingcar;

public class CarMoveCondition implements MoveCondition {

    private final int condition;

    public CarMoveCondition(int condition) {
        this.condition = condition;
    }

    @Override
    public boolean movable() {
        return condition >= 4;
    }
}
