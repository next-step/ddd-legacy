package racingcar;

/**
 * <pre>
 * racingcar
 *      ForwardMovingStrategy
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-03-01 오후 10:16
 */

public class HoldMovingStrategy implements MovingStrategy{
    @Override
    public boolean movable() {
        return false;
    }
}
