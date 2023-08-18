package racingcar;

import org.springframework.util.Assert;

public class Car {

	private static final String INVALID_NAME_LENGTH_MESSAGE = "자동차 이름은 5글자를 넘을 수 없습니다.";
	private final String name;
	private int position;

	public Car(String name) {
		validate(name);
		this.name = name;
		this.position = 0;
	}

	private static void validate(String name) {
		Assert.hasLength(name, "자동차 이름은 공백이나 null이 아닙니다.");
		Assert.isTrue(name.length() <= 5, INVALID_NAME_LENGTH_MESSAGE);
	}

	public void move(final MoveStrategy strategy) {
		if (strategy.isMovable()) {
			this.position++;
		}
	}

	public String getName() {
		return name;
	}

	public int getPosition() {
		return position;
	}
}
