package racingcar.domain;

public class Car {
	static final int CAR_NAME_MAX_LENGTH = 5;
	static final String CAR_NAME_EXCEEDED_MAXIMUM_LENGTH = "자동차 이름은 5글자를 넘을 수 없습니다.";

	private final String name;

	public Car(String name) {
		if (name.length() > CAR_NAME_MAX_LENGTH) {
			throw new IllegalArgumentException(CAR_NAME_EXCEEDED_MAXIMUM_LENGTH);
		}

		this.name = name;
	}
}
