package racingcar;

import java.util.Random;

public class RandomMovingStrategy implements MovingStrategy {
    @Override
    public boolean isMovable() {
        return new Random().nextInt(10) > 4;
    }
}
