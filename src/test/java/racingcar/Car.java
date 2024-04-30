package racingcar;

class Car {
	
	private final String name;
	private int position;
	
	public Car(String name) {
		this(name, 0);
	}
	
	public Car(String name, int position) {
		if (name.length() > 5) {
			throw new IllegalArgumentException();
		}
		this.name = name;
		this.position = position;
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
