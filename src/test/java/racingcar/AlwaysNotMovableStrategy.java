package racingcar;

public class AlwaysNotMovableStrategy implements MovingStrategy {
    @Override
    public boolean movable() {
        return false;
    }
}
