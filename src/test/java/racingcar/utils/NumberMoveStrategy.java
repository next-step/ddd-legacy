package racingcar.utils;

import racingcar.MoveStrategy;

public class NumberMoveStrategy implements MoveStrategy {
    private final int factor;

    public NumberMoveStrategy(int factor) {
        this.factor = factor;
    }

    @Override
    public boolean isMovable() {
        return factor > 3;
    }
}
