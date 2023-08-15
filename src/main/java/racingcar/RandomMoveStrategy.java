package racingcar;

import java.util.Random;

public class RandomMoveStrategy implements MoveStrategy {
    private static final int MOVE_CRITERIA = 4;
    private static final int UPPER_BOUND_NUM = 10;
    private static final Random random = new Random();

    @Override
    public boolean movable() {
        return random.nextInt(UPPER_BOUND_NUM) >= MOVE_CRITERIA;
    }
}
