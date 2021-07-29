package racingcar;

import java.util.Random;

public class RandomStrategy implements MovingStrategy{

    @Override
    public boolean movable() {
        return new Random().nextInt(10) < 4;
    }
}
