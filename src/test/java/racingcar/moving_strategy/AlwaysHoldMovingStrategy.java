package racingcar.moving_strategy;

public class AlwaysHoldMovingStrategy implements MovingStrategy {

    @Override
    public boolean movable() {
        return false;
    }
}
