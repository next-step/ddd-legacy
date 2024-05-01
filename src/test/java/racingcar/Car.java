package racingcar;

public class Car {

	private final String name;
	private final int position;

	private Car(final String name, final int position) {
		if (name.length() > 5) {
			throw new IllegalArgumentException("이름은 5글자를 넘을 수 없습니다");
		}

		this.name = name;
		this.position = position;
	}

	public Car(final String name) {
		this(name, 0);
	}

	public Car move(final int power) {
		if (power < 4) {
			return this;
		}
		
		return new Car(name, position + 1);
	}
}
