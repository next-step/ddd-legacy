package racingcar.strategy;

import java.util.Random;

public class RandomMoveStrategy implements MoveStrategy {
    private static final Random RANDOM = new Random();
    private static final int MAX_MOVABLE_VALUE = 10;
    private static final int MIN_ADVANCE_VALUE = 4;
    
    @Override
    public boolean isMovable() {
        return RANDOM.nextInt(MAX_MOVABLE_VALUE) >= MIN_ADVANCE_VALUE;
    }
}
