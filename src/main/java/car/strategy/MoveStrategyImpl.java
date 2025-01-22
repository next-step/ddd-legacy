package car.strategy;


public class MoveStrategyImpl implements MoveStrategy {
    private final int conditionNumber;

    private MoveStrategyImpl(final int conditionNumber) {
        this.conditionNumber = conditionNumber;
    }

    public static MoveStrategyImpl of(final int conditionNumber){
        return new MoveStrategyImpl(conditionNumber);
    }

    @Override
    public boolean movable(final int moveNumber) {
        return conditionNumber <= moveNumber;
    }

}
