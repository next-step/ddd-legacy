package racingcar.domain;

public class RandomStrategy implements MoveStrategy {
    @Override
    public boolean isMovable(int value) {
        return 4 <= value;
    }
}
