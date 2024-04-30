package racingcar;

import java.util.Random;

public class RandomStrategy implements MoveStrategy{
    @Override
    public int getDistance() {
        return new Random().nextInt(0,10);
    }
}
