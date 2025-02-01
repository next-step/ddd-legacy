package mission.step0.racingcar;

public class StopStrategy implements MovingStrategy {
    @Override
    public boolean movable() {
        return false;
    }
}
