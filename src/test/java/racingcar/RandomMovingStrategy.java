package racingcar;

public class RandomMovingStrategy implements MovingStrategy {

    private static final int MOVING_CONDITION = 4;
    private static final int RANDOM_RANGE = 10;

    private final RandomGenerator randomGenerator;

    public RandomMovingStrategy(final RandomGenerator randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

    @Override
    public boolean canMove() {
        if (randomGenerator.generate(RANDOM_RANGE) >= MOVING_CONDITION) {
            return true;
        }
        return false;
    }
}
