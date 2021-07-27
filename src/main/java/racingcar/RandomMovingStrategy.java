package racingcar;

import java.util.Random;

public class RandomMovingStrategy implements MovingStrategy {
    private static Random random = new Random();

    @Override
    public boolean isMovable() {
        int value = random.nextInt(10);
        return MovingStrategy.isMovable(value);
    }
}
