package racingcar;

public class HoldStrategy implements MovingStrategy {
    @Override
    public boolean canMove() {
        return false;
    }
}
