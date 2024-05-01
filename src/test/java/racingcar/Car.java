package racingcar;

public class Car {

	private final String name;

	public Car(final String name) {
		if (name.length() > 5) {
			throw new IllegalArgumentException("이름은 5글자를 넘을 수 없습니다");
		}
		
		this.name = name;
	}
}
