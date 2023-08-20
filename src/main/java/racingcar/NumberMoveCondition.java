package racingcar;

class NumberMoveCondition implements MoveCondition {

    private final int condition;

    NumberMoveCondition(int condition) {
        this.condition = condition;
    }

    @Override
    public boolean isMovable() {
        return condition >= 4;
    }
}