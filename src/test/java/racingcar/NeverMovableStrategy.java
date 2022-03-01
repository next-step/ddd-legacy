package racingcar;

public final class NeverMovableStrategy implements MovableStrategy {

    @Override
    public boolean movable() {
        return false;
    }
}
