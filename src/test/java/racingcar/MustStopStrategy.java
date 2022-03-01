package racingcar;

public class MustStopStrategy implements MovingStrategy{
    @Override
    public boolean movable() {
        return false;
    }
}
