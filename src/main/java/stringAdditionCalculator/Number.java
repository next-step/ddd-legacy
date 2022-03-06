package stringAdditionCalculator;

import java.util.Objects;

public class Number {
	public static final int ZERO = 0;
	public static final int MAXIMUM_VALUE = 100;
	private int number;
	private boolean isEmptyString;

	public Number(String stringNumber) {
		validateEmptiness(stringNumber);

		if (!isEmptyString) {
			validate(getIntegerValue(stringNumber));
		}
	}

	private int getIntegerValue(String stringNumber) {
		int value;

		try {
			value = Integer.parseInt(stringNumber);
		} catch (Exception e) {
			throw new RuntimeException("숫자로 변경할 수 없습니다");
		}

		return value;
	}

	private void validate(int inputValue) {
		validateMaximum(inputValue);
		validateNegative(inputValue);

		this.number = inputValue;
	}

	private void validateNegative(int inputValue) {
		if (inputValue < ZERO) {
			throw new RuntimeException("음수는 입력할 수 없습니다");
		}
	}

	private void validateMaximum(int inputValue) {
		if (inputValue >= MAXIMUM_VALUE) {
			throw new IllegalStateException("너무 큰 값을 입력하였습니다");
		}
	}

	private void validateEmptiness(String stringNumber) {
		if (stringNumber == null || stringNumber.isEmpty()) {
			this.number = ZERO;
			this.isEmptyString = true;
		}
	}

	public int getNumber() {
		return this.number;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Number number1 = (Number)o;
		return number == number1.number;
	}

	@Override
	public int hashCode() {
		return Objects.hash(number);
	}
}
