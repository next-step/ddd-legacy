package racingcar;

public class Car {
	private final String name;
	private int position;

	Car(String name) {
		if (name.length() > 5) {
			throw new IllegalArgumentException("자동차 이름은 5자 이하만 가능합니다.");
		}
		this.name = name;
		this.position = 0;
	}

	public void move(int condition) {
		if (condition >= 4) {
			position++;
		}
	}

	public int position() {
		return position;
	}
}
