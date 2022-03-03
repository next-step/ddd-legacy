package calculation.number;

import static calculation.ErrorMessage.NEGATIVE_NUMBER_ARE_NOT_ALLOWED;

public class Number {
	private final int value;

	public Number(int value) {
		if (value < 0) {
			throw new RuntimeException(NEGATIVE_NUMBER_ARE_NOT_ALLOWED + " : " + value);
		}
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public Number() {
		this(0);
	}

	public static int sum(int a, int b) {
		return a + b;
	}
}
