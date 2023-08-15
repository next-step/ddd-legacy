package racingcar;

class Car {
	private final String name;
	private int position;

	public Car(String car) {
		if (car.length() > 5) {
			throw new IllegalArgumentException("이름은 5자리를 넘을 수 없다.");
		}
		this.name = car;
	}

	public void move(MoveCondition moveCondition) {
		if (moveCondition.movable()) {
			position++;
		}
	}

	public String getName() {
		return name;
	}

	public int getPosition() {
		return position;
	}
}
