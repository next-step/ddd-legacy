package racingcar.strategy;

public class ForwardStrategy implements MovingStrategy {

    @Override
    public boolean movable() {
        return true;
    }
}
