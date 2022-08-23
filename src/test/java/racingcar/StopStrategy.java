package racingcar;

public class StopStrategy implements MoveStrategy {

    @Override
    public boolean isMovable() {
        return false;
    }
}
