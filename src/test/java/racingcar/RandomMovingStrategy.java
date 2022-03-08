package racingcar;

import java.security.SecureRandom;

public final class RandomMovingStrategy implements MovingStrategy {

    @Override
    public boolean movable() {
        return new SecureRandom().nextInt(10) >= 4;
    }
}
