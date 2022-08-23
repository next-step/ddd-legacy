package racingcar.strategy;

public class HoldStrategy implements MoveStrategy{
    @Override
    public boolean canMove() {
        return false;
    }
}
