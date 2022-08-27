package racingcar.source.strategy;

import java.util.Random;

public class RandomMoveStrategy implements MoveStrategy {
    public static final int RANDOM_BOUND = 10;
    public static final int CONDITION_BOUND = 4;
    private static final Random RANDOM = new Random();

    @Override
    public boolean movable() {
        int randomNumber = RANDOM.nextInt(RANDOM_BOUND);
        return randomNumber >= CONDITION_BOUND;
    }
}
