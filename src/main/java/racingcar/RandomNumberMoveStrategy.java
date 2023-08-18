package racingcar;

import java.util.Random;

public class RandomNumberMoveStrategy implements MoveStrategy {

	private static final Random RANDOM = new Random();
	private static final int MAX_BOUND = 9;
	private static final int MINIMUM_MOVABLE_NUMBER = 4;

	@Override
	public boolean isMovable() {
		return RANDOM.nextInt(MAX_BOUND) >= MINIMUM_MOVABLE_NUMBER;
	}
}
