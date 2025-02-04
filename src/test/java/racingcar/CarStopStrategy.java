package racingcar;

public class CarStopStrategy implements MovingStrategy {

    @Override
    public boolean movable() {
        return false;
    }
}
