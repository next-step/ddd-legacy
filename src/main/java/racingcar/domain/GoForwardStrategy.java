package racingcar.domain;

public class GoForwardStrategy implements MovingStrategy {
	@Override
	public boolean movable() {
		return true;
	}
}
