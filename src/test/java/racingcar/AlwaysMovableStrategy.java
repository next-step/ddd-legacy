package racingcar;

public final class AlwaysMovableStrategy implements MovableStrategy {

    @Override
    public boolean movable() {
        return true;
    }
}
