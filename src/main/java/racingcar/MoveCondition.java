package racingcar;

@FunctionalInterface
public interface MoveCondition {

    boolean movable();
}

class NumberMoveCondition implements MoveCondition {

    private final NumberGenerator numberGenerator;

    public NumberMoveCondition(NumberGenerator numberGenerator) {
        this.numberGenerator = numberGenerator;
    }

    @Override
    public boolean movable() {
        return numberGenerator.generate() >= 4;
    }
}