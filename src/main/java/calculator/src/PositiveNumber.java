package calculator.src;

import java.util.Objects;

public class PositiveNumber {

	public static final PositiveNumber ZERO = new PositiveNumber(0);

	private final int value;

	public PositiveNumber(int value) {
		validatePositive(value);
		this.value = value;
	}

	private void validatePositive(int value) {
		if (value < 0) {
			throw new RuntimeException();
		}
	}

	public static PositiveNumber valueOf(String value) {
		return new PositiveNumber(toNumeric(value));
	}

	private static int toNumeric(String value) {
		if(value == null || value.isBlank()) {
			return 0;
		}

		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new RuntimeException(e);
		}
	}

	public PositiveNumber sum(PositiveNumber positiveNumber) {
		return new PositiveNumber(this.value + positiveNumber.value);
	}

	public int intValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		PositiveNumber that = (PositiveNumber) o;
		return value == that.value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}
