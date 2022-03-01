package racingcar;

public class ImmovableStrategy implements MovingStrategy {

	@Override
	public boolean movable() {
		return false;
	}
}
