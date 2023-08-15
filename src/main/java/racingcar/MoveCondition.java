package racingcar;

@FunctionalInterface
public interface MoveCondition {
    boolean movable();
}

class NumberMoveCondition implements MoveCondition {
    private final int condition;

    public NumberMoveCondition(int condition) {
        this.condition = condition;
    }

    @Override
    public boolean movable() {
        return condition > 4;
    }
}