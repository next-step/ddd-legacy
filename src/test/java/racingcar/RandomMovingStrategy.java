package racingcar;

import java.util.Random;

/**
 * <pre>
 * racingcar
 *      RandomMovingStrategy
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-03-01 오후 10:13
 */

public class RandomMovingStrategy implements MovingStrategy{

    @Override
    public boolean movable() {
        return new Random().nextInt(10) >= 4;
    }
}
