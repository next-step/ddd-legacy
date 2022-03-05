package stringAdditionCalculator;

import java.util.Objects;

public class Number {
	public static final int ZERO = 0;
	public static final int MAXIMUM_VALUE = 100;
	private int number;
	private boolean isEmptyString;

	public Number(String stringNumber) {
		validateStringNumber(stringNumber);

		if (!isEmptyString) {
			validateValue(Integer.parseInt(stringNumber));
		}
	}

	private void validateValue(int inputValue) {
		if (inputValue >= MAXIMUM_VALUE) {
			throw new IllegalStateException("너무 큰 값을 입력하였습니다");
		}

		this.number = inputValue;
	}

	private void validateStringNumber(String stringNumber) {
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
