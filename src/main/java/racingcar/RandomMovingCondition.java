package racingcar;

public class RandomMovingCondition implements MovingCondition {

    @Override
    public boolean isMovePossible(int number) {
        return number >= 4;
    }
}
