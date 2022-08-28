package racingcar.moving_strategy;

public class AlwaysMoveMovingStrategy implements MovingStrategy {

    @Override
    public boolean movable() {
        return true;
    }
}
