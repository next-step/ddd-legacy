package racingcar;

import org.springframework.util.Assert;

public class Car {

	private static final String INVALID_NAME_LENGTH_MESSAGE = "자동차 이름은 5글자를 넘을 수 없습니다.";
	private final String name;

	private Car(String name) {
		validate(name);
		this.name = name;
	}

	public static Car from(String name) {
		return new Car(name);
	}

	private static void validate(String name) {
		Assert.hasLength(name, "자동차 이름은 공백이나 null이 아닙니다.");
		Assert.isTrue(name.length() <= 5, INVALID_NAME_LENGTH_MESSAGE);
	}
}
