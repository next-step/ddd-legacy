package racingcar;

import java.util.Random;

public class RandomMovingStrategy {
    static final int MOVABLE_THRESHOLD = 4;
    static final int RANDOM_VALUE_BOUND = 9;

    private final Random random;

    RandomMovingStrategy() {
        this(new Random());
    }

    public RandomMovingStrategy(Random random) {
        this.random = random;
    }

    boolean movable() {
        return random.nextInt(RANDOM_VALUE_BOUND) >= MOVABLE_THRESHOLD;
    }
}
