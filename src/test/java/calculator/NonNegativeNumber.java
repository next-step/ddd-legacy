package calculator;

public record NonNegativeNumber(int value) {

	public NonNegativeNumber {
		if (value < 0) {
			throw new IllegalArgumentException("Negative number not allowed: " + value);
		}
	}

	public static NonNegativeNumber from(final String inputValue) {
		try {
			return new NonNegativeNumber(Integer.parseInt(inputValue));
		} catch (final NumberFormatException e) {
			throw new IllegalArgumentException("Character not allowed: " + inputValue, e);
		}
	}
}
