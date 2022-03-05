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

	public Numbers(List<Number> numbers) {
		this.numbers = numbers;
	}

	public void addNumber(Number number) {
		if (numbers.size() >= MAXIMUM_SIZE) {
			throw new IllegalStateException("너무 많은 값을 입력하였습니다.");
		}
		this.numbers.add(number);
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
