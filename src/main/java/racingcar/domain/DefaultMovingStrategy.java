package racingcar.domain;

import java.security.SecureRandom;

public class DefaultMovingStrategy implements MovingStrategy {
	private static final int RANDOM_BOUND_VALUE = 10;

	private static final int MINIMUM_MOVABLE_NUMBER = 4;

	private final SecureRandom random = new SecureRandom();

	@Override
	public boolean movable() {
		int randomNumber = random.nextInt(RANDOM_BOUND_VALUE);
		return randomNumber >= MINIMUM_MOVABLE_NUMBER;
	}
}
