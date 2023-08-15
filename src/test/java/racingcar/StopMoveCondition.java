package racingcar;

class StopMoveCondition implements MoveCondition {

    @Override
    public boolean movable() {
        return false;
    }
}
