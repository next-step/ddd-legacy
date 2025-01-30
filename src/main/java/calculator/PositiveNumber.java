package calculator;

public class PositiveNumber {

	private final int value;

	public PositiveNumber(String input) {
		this.value = parseStringToNumber(input);
	}

	public int getValue() {
		return value;
	}

	private int parseStringToNumber(String input) {
		try {
			int number = Integer.parseInt(input);
			throwExceptionIfNegativeNumber(number);
			return number;
		} catch (NumberFormatException numberFormatException) {
			throw new RuntimeException(ErrorMessage.NOT_NUMBER);
		}
	}

	private void throwExceptionIfNegativeNumber(int number) {
		if (number < 0) {
			throw new RuntimeException(ErrorMessage.NEGATIVE_NUMBER);
		}
	}
}
