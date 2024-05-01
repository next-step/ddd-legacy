package racingcar;

public class RandomMovingStrategy implements MovingStrategy {
    @Override
    public boolean movable(int number) {
        return number >= 4;
    }
}
