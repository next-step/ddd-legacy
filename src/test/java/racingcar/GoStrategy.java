package racingcar;

public class GoStrategy implements MovingStrategy {
    final int goCondition;

    public GoStrategy(int goCondition) {
        if (goCondition <= 0) {
            throw new IllegalArgumentException("condition should be over 0, given " + goCondition);
        }
        this.goCondition = goCondition;
    }


    @Override
    public boolean movable(int condition) {
        return condition >= this.goCondition;
    }
}