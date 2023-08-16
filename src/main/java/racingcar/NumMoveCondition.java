package racingcar;

class NumMoveCondition implements MoveCondition {
    private final int condition;

    public NumMoveCondition(int condition) {
        this.condition = condition;
    }

    @Override
    public boolean movable() {
        return condition >= 4;
    }
}
