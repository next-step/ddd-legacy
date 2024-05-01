package racingcar;

import java.util.Random;

public class RandomMovingStrategy implements MovingStrategy {

    private final static int MOVE_THRESHOLD = 4;
    private final Random RANDOM_GENERATOR = new Random();

    @Override
    public boolean canMove() {
        return MOVE_THRESHOLD <= RANDOM_GENERATOR.nextInt(10) + 1;
    }
}
