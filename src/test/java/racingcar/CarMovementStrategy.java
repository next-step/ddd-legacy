package racingcar;

import java.util.Random;

public class CarMovementStrategy implements MovementStrategy{
    @Override
    public boolean move() {
        Random random = new Random();
        return random.nextInt(0, 9) >= 4;
    }
}
