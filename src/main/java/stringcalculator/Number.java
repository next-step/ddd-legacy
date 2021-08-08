package stringcalculator;

import java.util.Objects;

public class Number {

	private final String number;

	public Number(String number) {

		if (Integer.parseInt(number) < 0) {
			throw new RuntimeException();
		}

		this.number = number;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Number number1 = (Number)o;
		return Objects.equals(number, number1.number);
	}

	@Override
	public int hashCode() {
		return Objects.hash(number);
	}

	public Integer toInteger() {
		return Integer.parseInt(number);
	}
}
