package racingcar.strategy;

import java.util.Random;

public class RandomMoveStrategy implements MoveStrategy {
    private static final int MOVE_CONDITION = 4;
    private static final int BOUND = 10;

    private static final Random RANDOM = new Random();

    @Override
    public boolean canMove() {
        return RANDOM.nextInt(BOUND) > MOVE_CONDITION;
    }
}
