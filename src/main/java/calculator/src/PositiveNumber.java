package calculator.src;

import java.util.Objects;

public class PositiveNumber {

	private final long value;

	public PositiveNumber(String value) {
		this.value = toNumeric(value);
	}

	private long toNumeric(String value) {
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			throw new RuntimeException(e);
		}
	}

	public PositiveNumber(int value) {
		this((long) value);
	}

	public PositiveNumber(long value) {
		validatePositive(value);
		this.value = value;
	}

	private void validatePositive(long value) {
		if (value < 0) {
			throw new RuntimeException();
		}
	}

	public PositiveNumber sum(PositiveNumber positiveNumber) {
		return new PositiveNumber(this.value + positiveNumber.value);
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
