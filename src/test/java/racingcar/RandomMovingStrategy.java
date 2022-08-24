package racingcar;

import java.util.Random;

public class RandomMovingStrategy implements MovingStrategy {
    @Override
    public boolean isMovable(int position) {
        return (new Random().nextInt(10) >= _MOVABLE_POSITION_VALUE);
    }
}
