package racingcar;

public class HoldStrategy implements MovingStrategy {
    @Override
    public boolean isMovable() {
        return false;
    }
}
