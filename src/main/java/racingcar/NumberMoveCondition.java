package racingcar;

public class NumberMoveCondition implements MoveCondition {
    private final int condition;

    public NumberMoveCondition(int condition) {
        this.condition = condition;
    }

    @Override
    public boolean movable() {
        return this.condition >= 4;
    }
}
