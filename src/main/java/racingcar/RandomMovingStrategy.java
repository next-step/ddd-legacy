package racingcar;

import java.util.Random;

public class RandomMovingStrategy implements MovingStrategy {

    @Override
    public boolean movable() {
        return new Random().nextInt(9) >= 4;
    }
}
