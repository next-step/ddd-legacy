package stringaddcalculator;

public class StringNumber {

	private final String value;

	private StringNumber(String value) {
		this.value = value;
	}

	public static StringNumber from(String value) {
		return new StringNumber(value);
	}

	public int parseInt() {
		if (isEmpty()) {
			return 0;
		}
		return parseValidInt();
	}

	private boolean isEmpty() {
		return value == null || value.trim().isEmpty();
	}

	private int parseValidInt() {
		int number = Integer.parseInt(value);
		validate(number);
		return number;
	}

	private void validate(int number) {
		if (isNegative(number)) {
			throw new IllegalArgumentException(String.format("Number(%d) must not be negative", number));
		}
	}

	private boolean isNegative(int number) {
		return number < 0;
	}

}
