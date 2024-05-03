package racingcar;

import java.util.Random;

public class RandomNumberGenerator implements NumberGenerator {

    @Override
    public int generateInt() {
        Random random = new Random();
        return random.nextInt(0, 10);
    }
}
