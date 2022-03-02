package calculation.number;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Numbers {
	private final List<Number> numbers = new ArrayList<>();

	public List<Number> getNumbers() {
		return numbers;
	}

	public void addNumber(Number number) {
		getNumbers().add(number);
	}

	public Numbers convertStringsToIntegers(String[] stringNumbers) {
		Arrays.stream(stringNumbers).map(Number::convert).forEach(this::addNumber);
		return this;
	}

	public int sum() {
		return numbers.stream().mapToInt(Number::getNumber).sum();
	}

	public void addZero() {
		getNumbers().add(new Number(0));
	}
}
