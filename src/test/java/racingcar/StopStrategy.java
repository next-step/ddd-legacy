package racingcar;

public class StopStrategy implements MoveStrategy {
    @Override
    public boolean movable() {
        return false;
    }
}
