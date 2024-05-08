package calculator;

public record InputNumber(int value) {

	public InputNumber {
		if (value < 0) {
			throw new IllegalArgumentException("Negative number not allowed: " + value);
		}
	}

	public static InputNumber from(final String inputValue) {
		return new InputNumber(Integer.parseInt(inputValue));
	}
}
