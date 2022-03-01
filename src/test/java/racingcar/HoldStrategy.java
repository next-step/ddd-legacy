package racingcar;

public class HoldStrategy implements MovingStrategy{
    @Override
    public boolean movable() {
        return false;
    }
}
