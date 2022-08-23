package racingcar.strategy;

public class ForwardStrategy implements MoveStrategy {
    @Override
    public boolean canMove() {
        return true;
    }
}
