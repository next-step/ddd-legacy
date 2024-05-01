package racingcar;

public class Car {

	private static final int POWER_THRESHOLD = 4;
	private final Name name;
	private final int position;

	private Car(final Name name, final int position) {
		this.name = name;
		this.position = position;
	}

	public Car(final String name) {
		this(new Name(name), 0);
	}

	public Car move(final int power) {
		return move(() -> power >= POWER_THRESHOLD);
	}

	public Car move(final MovingStrategy movingStrategy) {
		if (movingStrategy.movable()) {
			return new Car(name, position + 1);
		}

		return this;
	}
}
