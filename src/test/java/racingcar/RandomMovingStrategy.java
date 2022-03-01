package racingcar;

import java.util.Random;

public class RandomMovingStrategy implements MovingStrategy{
    @Override
    public boolean movable() {
        if(new Random().nextInt(10) >=4) return true;
        return false;
    }
}
