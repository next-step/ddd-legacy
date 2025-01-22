package racingcar.strategy;

import racingcar.MovingStrategy;

import java.util.Random;

public class RandomMovingStrategy implements MovingStrategy {

    private static final int MOVING_CONDITION_VALUE = 4;

    @Override
    public boolean movable() {
        final int conditionValue = new Random().nextInt(10);

        return conditionValue >= MOVING_CONDITION_VALUE;
    }
}
