package racingcar.domain;

public class Car {
	static final int CAR_NAME_MAX_LENGTH = 5;
	static final String CAR_NAME_SHOULD_NOT_BE_EMPTY = "자동차 이름이 존재해야 합니다.";
	static final String CAR_NAME_SHOULD_NOT_EXCEED_MAXIMUM_LENGTH = "자동차 이름은 5글자를 넘을 수 없습니다.";

	private final String name;
	private int position;

	public Car(final String name) {
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException(CAR_NAME_SHOULD_NOT_BE_EMPTY);
		}

		if (name.length() > CAR_NAME_MAX_LENGTH) {
			throw new IllegalArgumentException(CAR_NAME_SHOULD_NOT_EXCEED_MAXIMUM_LENGTH);
		}

		this.name = name;
		this.position = 0;
	}

	public void move(final MovingStrategy movingStrategy) {
		if (movingStrategy.movable()) {
			this.position++;
		}
	}

	public int position() {
		return position;
	}
}
