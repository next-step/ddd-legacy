package racingcar.source.strategy;

public class ForwardMoveStrategy implements MoveStrategy {
    @Override
    public boolean movable() {
        return true;
    }
}
