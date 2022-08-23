package racingcar;

import java.util.Random;

public class RandomMovingStrategy implements MovingStrategy{

    private static final int MIN_OF_FORWARD_MOVE = 4;

    @Override
    public boolean movable() {
        int condition = new Random().nextInt(10);
        return condition > MIN_OF_FORWARD_MOVE;
    }
}
