package racingcar;

import java.util.Random;

public class RandomMovingStrategy implements MovingStrategy {
    private static final Random RANDOM = new Random();
    private static final int BOUND = 10;
    private static final int THRESH_HOLD = 4;

    @Override
    public boolean moveAble() {
        return RANDOM.nextInt(BOUND) >= THRESH_HOLD;
    }
}
