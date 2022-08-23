package racingcar;

public class HoldStrategy implements MoveStrategy {

	@Override
	public boolean isMovable() {
		return false;
	}
}