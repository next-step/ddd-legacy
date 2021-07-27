package study;

import java.util.Random;

public class RandomMovingStrategy implements MovingStratgy {
    @Override
    public boolean movable() {
        return new Random().nextInt(10) >= 4;
    }
}
