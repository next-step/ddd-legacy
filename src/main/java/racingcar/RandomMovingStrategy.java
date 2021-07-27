package racingcar;

import java.util.Random;

public class RandomMovingStrategy implements MovingStrategy {
    private static Random random = new Random();

    @Override
    public boolean isMovable() {
        return random.nextInt(10) >= 4;
    }
}
