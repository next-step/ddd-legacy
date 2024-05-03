package racingcar;

import java.util.Random;

public class RandomMovingStrategy implements MovingStrategy {
    @Override
    public boolean movable() {
        Random rand = new Random(System.currentTimeMillis());
        int number = rand.nextInt(10);
        return number >= 4;
    }
}
