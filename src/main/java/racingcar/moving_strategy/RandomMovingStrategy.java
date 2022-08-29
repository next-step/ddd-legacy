package racingcar.moving_strategy;

import java.util.Random;

public class RandomMovingStrategy implements MovingStrategy {

    private static final int RANDOM_LIMIT = 10;

    private static final int MOVING_THRESHOLD = 4;

    private final Random random = new Random();

    @Override
    public boolean movable() {
        int condition = this.random.nextInt(RANDOM_LIMIT);
        return condition >= MOVING_THRESHOLD;
    }
}
