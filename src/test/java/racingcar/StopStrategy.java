package racingcar;

public class StopStrategy implements MovingStrategy {
    final int stopCondition;

    public StopStrategy(int stopCondition) {
        if (stopCondition <= 0) {
            throw new IllegalArgumentException("condition should be over 0, given " + stopCondition);
        }
        this.stopCondition = stopCondition;
    }

    @Override
    public boolean movable(int condition) {
        return condition < this.stopCondition;
    }
}