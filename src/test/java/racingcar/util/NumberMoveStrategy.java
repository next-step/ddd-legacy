package racingcar.util;

import racingcar.MoveStrategy;

public class NumberMoveStrategy implements MoveStrategy {
    private static final int MOVE_CRITERIA = 4;

    private final int count;

    public NumberMoveStrategy(final int count) {
        this.count = count;
    }

    @Override
    public boolean movable() {
        return count >= MOVE_CRITERIA;
    }
}
