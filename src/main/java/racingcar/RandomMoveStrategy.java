package racingcar;

public class RandomMoveStrategy implements MoveStrategy {

    private static final int RANDOM_MOVE_NUMBER = 4;

    @Override
    public boolean movable(int number) {
        return number >= RANDOM_MOVE_NUMBER;
    }
}
