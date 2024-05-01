package racingcar;

public class StopStrategy implements MovingStrategy {
    @Override
    public boolean moveable() {
        return false;
    }
}


