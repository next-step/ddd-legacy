package racingcar;

import java.util.Random;

public class RandomMoveStrategy implements MoveStrategy {

    private static final int MOVABLE_BOUND = 4;
    private static final int MAX_VALUE = 10;
    private static final Random random = new Random();

    @Override
    public boolean isMovable() {
        return random.nextInt(MAX_VALUE) >= MOVABLE_BOUND;
    }
}
