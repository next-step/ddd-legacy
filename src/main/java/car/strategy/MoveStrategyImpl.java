package car.strategy;


public class MoveStrategyImpl implements MoveStrategy {
    private final int conditionNumber;

    public MoveStrategyImpl(final int conditionNumber) {
        this.conditionNumber = conditionNumber;
    }

    @Override
    public boolean movable(final int moveNumber) {
        return conditionNumber <= moveNumber;
    }

}
