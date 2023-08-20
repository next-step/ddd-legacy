package stringaddcalculator;

import java.util.List;

public class StringNumbers {

	private final List<StringNumber> numbers;

	private StringNumbers(List<StringNumber> numbers) {
		this.numbers = numbers;
	}

	public static StringNumbers from(List<StringNumber> numbers) {
		return new StringNumbers(numbers);
	}

	public int sum() {
		int sum = 0;
		for (StringNumber number : numbers) {
			sum += number.parseInt();
		}
		return sum;

	}
}
