package racingcar;

@FunctionalInterface
public interface MovingStrategy {
    boolean isMovable();

    static boolean isMovable(int value) {
        return value >= 4;
    }
}
