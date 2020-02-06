package racingcar;

import java.util.Random;

public class RandomMovingStrategy {

    private final Random random;

    RandomMovingStrategy() {
        this(new Random());
    }

    public RandomMovingStrategy(Random random) {
        this.random = random;
    }

    boolean movable() {
        return random.nextInt(9) >= 4;
    }
}
