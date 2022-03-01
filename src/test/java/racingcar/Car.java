package racingcar;

public class Car {
	private String name;
	private int position;

	public Car(String name) {
		this(name, 0);
	}

	private void validation(final String name) {
		if (name.length() >= 5) {
			throw new IllegalArgumentException("자동차 이름은 5글자를 넘을 수 없다");
		}
	}

	public Car(String name, int position) {
		validation(name);
		this.name = name;
		this.position = position;
	}

	public int getPosition() {
		return position;
	}

	public void move(final int number) {
		if (number >= 4) {
			position++;
		}
	}

	public void move(final MovingStrategy movingStrategy) {
		if (movingStrategy.movable()) {
			position++;
		}
	}
}
