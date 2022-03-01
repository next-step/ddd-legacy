package racingcar;

import java.util.Random;

public class RandomMovingStrategy implements MovingStrategy{
    private final Random random = new Random();

    public boolean movable() {
        return random.nextInt(10) >= 4;
    }
}
