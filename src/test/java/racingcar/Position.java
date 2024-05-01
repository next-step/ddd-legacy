package racingcar;

public record Position(int value) {

	public static final Position INITIAL = new Position(0);
	
	public Position next() {
		return new Position(value + 1);
	}
}
