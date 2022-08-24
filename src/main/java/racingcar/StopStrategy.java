package racingcar;

public class StopStrategy implements MovingStrategy {
    @Override
    public boolean isMovable(int position) {
        return (position < 4);
    }
}
