package racingcar;

public class ThresholdStrategy implements MoveStrategy {

    private final int threshold;

    public ThresholdStrategy(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public CarPosition getNextPosition(CarPosition position, int input) {
        if (input >= threshold) {
            return position.move(1);
        }
        return position;
    }
}
