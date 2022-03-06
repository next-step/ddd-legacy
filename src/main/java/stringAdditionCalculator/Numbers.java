package stringAdditionCalculator;

import java.util.ArrayList;
import java.util.List;

public class Numbers {
	public static final int EMPTY = 0;
	public static final int MAXIMUM_SIZE = 100;
	List<Number> numbers;

	public Numbers() {
		this.numbers = new ArrayList<>();
	}

	public Numbers(String[] stringNumbers) {
		this();
		validateListEmptiness(stringNumbers);

		for (String number : stringNumbers) {
			addNumber(new Number(number));
		}
	}

	private void validateListEmptiness(String[] stringNumbers) {
		if (stringNumbers == null || stringNumbers.length == EMPTY) {
			throw new IllegalArgumentException("null 혹은 빈 값은 입력할 수 없습니다");
		}
	}

	public void addNumber(Number number) {
		validateSize();
		validateEmptiness(number);
		this.numbers.add(number);
	}

	private void validateEmptiness(Number number) {
		if (number == null) {
			throw new IllegalArgumentException("null은 추가할 수 없습니다");
		}
	}

	private void validateSize() {
		if (numbers.size() >= MAXIMUM_SIZE) {
			throw new IllegalArgumentException("너무 많은 값을 입력하였습니다.");
		}
	}

	public int getSum() {
		if (numbers.size() == EMPTY) {
			return Number.ZERO;
		}

		int sum = Number.ZERO;

		for (Number number : this.numbers) {
			sum += number.getNumber();
		}

		return sum;
	}
}
