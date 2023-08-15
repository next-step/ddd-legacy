package racingcar;

public class NumberMoveCondition implements MoveCondition {
    private final int condition;

    @Override
    public boolean isMovable() {
        return condition >= 4;
    }

    public NumberMoveCondition(int condition) {
        this.condition = condition;
    }
}
