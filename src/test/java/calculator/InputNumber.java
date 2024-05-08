package calculator;

public record InputNumber(int value) {

	public InputNumber {
		if (value < 0) {
			throw new IllegalArgumentException("Negative number not allowed: " + value);
		}
	}

	public static InputNumber from(final String inputValue) {
		try {
			return new InputNumber(Integer.parseInt(inputValue));
		} catch (final NumberFormatException e) {
			throw new IllegalArgumentException("Character Not Allowed: " + inputValue, e);
		}
	}
}
