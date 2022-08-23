package racingcar;

public class Car {

	private static final int MAXIMUM_NAME_LENGTH = 5;

	private final String name;
	private int position;

	public Car(String name) {
		this(name, 0);
	}

	public Car(String name, final int position) {
		if (name == null) {
			throw new IllegalArgumentException("자동차 이름은 비어있을 수 없습니다.");
		}
		if (name.isBlank()) {
			throw new IllegalArgumentException("자동차 이름은 비어있을 수 없습니다.");
		}
		if (name.length() > MAXIMUM_NAME_LENGTH) {
			throw new IllegalArgumentException("자동차 이름은 5글자를 넘을 수 없습니다.");
		}

		this.name = name;
		this.position = position;
	}

	public void move(final MoveStrategy moveStrategy) {
		if (moveStrategy.isMovable()) {
			this.position++;
		}
	}

	public int getPosition() {
		return position;
	}
}