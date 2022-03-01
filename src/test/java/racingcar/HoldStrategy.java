package racingcar;

public class HoldStrategy implements MovingStrategy {
    @Override
    public boolean moveable() {
        return false;
    }
}
