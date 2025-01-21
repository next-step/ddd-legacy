package racingcar.strategy;

import racingcar.MovingStrategy;

public class StopMovingStrategy implements MovingStrategy {
    @Override
    public boolean movable() {
        return false;
    }
}
