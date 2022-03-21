package racingcar;

import java.util.Objects;

public class Car {

	private final String name;
	private final int position;

	public Car(String name, int position) {
		validateName(name);
		this.name = name;
		this.position = position;
	}

	public Car(String name) {
		this(name, 0);
	}

	private void validateName(String name) {
		if (name.length() > 5) {
			throw new IllegalArgumentException();
		}
	}

	public Car move(MovingStrategy movingStrategy) {
		if (movingStrategy.movable()) {
			return new Car(name, this.position + 1);
		}

		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Car car = (Car) o;
		return position == car.position && Objects.equals(name, car.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, position);
	}
}
