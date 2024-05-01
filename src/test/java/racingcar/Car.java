package racingcar;

public class Car {

	private static final int POWER_THRESHOLD = 4;
	private final Name name;
	private final Position position;

	private Car(final Name name, final Position position) {
		this.name = name;
		this.position = position;
	}

	public Car(final String name) {
		this(new Name(name), Position.INITIAL);
	}

	public Car move(final int power) {
		return move(() -> power >= POWER_THRESHOLD);
	}

	public Car move(final MovingStrategy movingStrategy) {
		if (movingStrategy.movable()) {
			return new Car(name, position.next());
		}

		return this;
	}

	public int getPosition() {
		return position.value();
	}
}
