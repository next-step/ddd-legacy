package racingcar;

import java.util.Random;

public class RandomMovingStrategy implements MovingStrategy {
    private static final int MOVING_CONDITION = 4;
    private static final int RANDOM_RANGE = 10;

    private final Random random = new Random();

    @Override
    public boolean canMove() {
        if (random.nextInt(RANDOM_RANGE) >= MOVING_CONDITION) {
            return true;
        }
        return false;
    }
}
