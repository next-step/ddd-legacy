package study;

public class StopStrategy implements MovingStrategy {
    @Override
    public boolean isMovable() {
        return false;
    }
}
