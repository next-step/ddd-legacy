package racingcar;

public class HoldMovingStrategy implements MovingStrategy {
    @Override
    public boolean movable() {
        return false;
    }
}
