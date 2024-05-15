package calculator;

public class PositiveNumber {
	private final int number;

	public PositiveNumber(String text) {
		if (Integer.parseInt(text) < 0) {
			throw new RuntimeException();
		}
		number = Integer.parseInt(text);
	}

	public int getNumber() {
		return number;
	}
}
