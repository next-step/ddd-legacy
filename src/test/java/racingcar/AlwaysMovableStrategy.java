package racingcar;

public class AlwaysMovableStrategy implements MovingStrategy {

    @Override
    public boolean movable() {
        return true;
    }
}
