package racingcar;

import java.util.Random;

// 전략패턴이래!
public class RandomMovingStrategy implements MovingStrategy {
    @Override
    public boolean movable() {
        return new Random().nextInt(9) >= 4;
    }
}