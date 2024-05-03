package racingcar;

public class NumberCarMovingStrategy implements CarMovingStrategy {
    private static final int MOVE_THRESH_HOLD = 4;

    private final NumberGenerator numberGenerator;

    public NumberCarMovingStrategy(NumberGenerator numberGenerator) {
        this.numberGenerator = numberGenerator;
    }

    @Override
    public boolean isMovable() {
        int moveConstant = numberGenerator.generateInt();
        return moveConstant >= MOVE_THRESH_HOLD;
    }
}
