package calculator;

import java.util.Objects;

public class PositiveNumber {

	private final int value;

	public PositiveNumber(int value) {
		if (value < 0) {
			throw new RuntimeException("PositiveNumber의 값은 0보다 작을 수 없습니다.");
		}
		this.value = value;
	}

	public int value() {
		return value;
	}

	public PositiveNumber plus(PositiveNumber other) {
		return new PositiveNumber(this.value + other.value);
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