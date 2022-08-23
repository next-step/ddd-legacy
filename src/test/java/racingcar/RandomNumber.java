package racingcar;

import java.util.Random;

public class RandomNumber implements RandomGenerator {

    private static final Random random = new Random();

    @Override
    public int generate(int bound) {
        return random.nextInt(bound);
    }
}
