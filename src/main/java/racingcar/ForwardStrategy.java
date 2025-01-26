package racingcar;

public class ForwardStrategy implements MovingStrategy {

    private final int condition;

    public ForwardStrategy(int condition) {
        this.condition = condition;
    }

    @Override
    public boolean movable() {
        return condition >= 4;
    }
}