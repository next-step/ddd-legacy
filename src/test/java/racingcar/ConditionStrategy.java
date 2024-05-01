package racingcar;

public class ConditionStrategy implements MovingStrategy {
	private int condition;

	public ConditionStrategy(int condition) {
		this.condition = condition;
	}

	@Override
	public boolean isMovable() {
		return condition >= 4;
	}

}
