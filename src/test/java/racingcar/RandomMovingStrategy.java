package racingcar;

import java.util.Random;

public class RandomMovingStrategy implements MovingStrategy {
    @Override
    public boolean moveable() {
        return new Random().nextInt(10) >= 4;
    }
}
