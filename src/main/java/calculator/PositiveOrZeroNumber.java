package calculator;

import java.util.Objects;

public class PositiveOrZeroNumber {

	private final int value;

	public PositiveOrZeroNumber(final int value) {
		validate(value);
		this.value = value;
	}

	private void validate(final int value) {
		if (value < 0) {
			throw new IllegalArgumentException("0보다 작은 값으로 생성할 수 없습니다. value: " + value);
		}
	}

	public PositiveOrZeroNumber plus(final PositiveOrZeroNumber that) {
		return new PositiveOrZeroNumber(this.value + that.value);
	}

	public int getValue() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final PositiveOrZeroNumber that = (PositiveOrZeroNumber) o;
		return value == that.value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}
