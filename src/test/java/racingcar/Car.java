package racingcar;

import org.springframework.lang.NonNull;

public class Car {

	private static final int MAX_NAME_LENGTH = 5;

	private final String name;
	private int position;

	public Car(@NonNull final String name) {
		this(name, 0);
	}

	public Car(@NonNull final String name, final int position) {
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("자동차 이름은 null 이거나 빈 문자열일 수 없습니다");
		}

		if (name.length() > MAX_NAME_LENGTH) {
			throw new IllegalArgumentException("자동차 이름은 5 글자를 넘을 수 없습니다");
		}
		this.name = name;
		this.position = position;
	}

	public void move(@NonNull final MovingStrategy movingStrategy) {
		if (movingStrategy.isMovable()) {
			position++;
		}
	}

	public int getPosition() {
		return position;
	}
}

/*
자동차의 이동 방식을 다양하게 구현할 수 있도록 Strategy Pattern 사용
 */