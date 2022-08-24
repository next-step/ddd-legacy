package racingcar.testdouble;

import racingcar.MovingStrategy;

public class HoldMovingStrategy implements MovingStrategy {

    @Override
    public boolean movable() {
        return false;
    }
}
