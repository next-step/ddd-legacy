package racingcar.source.strategy;

public class HoldMoveStrategy implements MoveStrategy {
    @Override
    public boolean movable() {
        return false;
    }
}
