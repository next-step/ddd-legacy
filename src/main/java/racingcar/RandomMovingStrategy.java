package racingcar;

import java.util.Random;

public class RandomMovingStrategy implements MovingStrategy {
    static final int MOVABLE_THRESHOLD = 4;
    static final int RANDOM_VALUE_BOUND = 9;

    private final Random random;

    public RandomMovingStrategy(Random random) {
        this.random = random;
    }

    @Override
    public boolean movable() {
        return random.nextInt(RANDOM_VALUE_BOUND) >= MOVABLE_THRESHOLD;
    }
}
