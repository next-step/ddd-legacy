package racingcar.strategy;

import racingcar.MovingStrategy;

public class ForwardMovingStrategy implements MovingStrategy {
    @Override
    public boolean movable() {
        return true;
    }
}
