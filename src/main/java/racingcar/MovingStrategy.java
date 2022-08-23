package racingcar;

@FunctionalInterface
public interface MovingStrategy {

    boolean movable();

    static boolean forward() {
        return true;
    }

    static boolean hold() {
        return false;
    }
}
