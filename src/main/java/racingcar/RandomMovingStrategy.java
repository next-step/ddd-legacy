package racingcar;

import java.util.Random;

public class RandomMovingStrategy {
    boolean movable() {
        return new Random().nextInt(9) >= 4;
    }
}
