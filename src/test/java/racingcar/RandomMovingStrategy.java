package racingcar;

import java.util.Random;

public class RandomMovingStrategy implements MovingStrategy {
    private final Random random = new Random();

    @Override
    public boolean movable() {
        int num = random.nextInt(10);
        return num >= 4;
    }
}
