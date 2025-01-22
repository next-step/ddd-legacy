package car.strategy;

@FunctionalInterface
public interface MoveStrategy {

    boolean movable(int moveNumber);

}
