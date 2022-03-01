package racingcar;

import java.util.Random;

public final class RandomMovableStrategy implements MovableStrategy {

    private static final int MOVABLE_THRESHOLD = 4;
    private static final int RANDOM_MAX_BOUND = 10;

    private final Random random;

    public RandomMovableStrategy() {
        this(new Random());
    }

    public RandomMovableStrategy(Random random) {
        this.random = random;
    }

    @Override
    public boolean movable() {
        return random.nextInt(RANDOM_MAX_BOUND) >= MOVABLE_THRESHOLD;
    }
}
