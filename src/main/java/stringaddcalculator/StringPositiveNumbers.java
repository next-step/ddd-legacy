package stringaddcalculator;

import java.util.List;
import java.util.stream.Collectors;

public class StringPositiveNumbers {

	private final List<StringPositiveNumber> numbers;

	private StringPositiveNumbers(List<StringPositiveNumber> numbers) {
		this.numbers = numbers;
	}

	public static StringPositiveNumbers fromStringCollection(List<String> numbers) {
		return new StringPositiveNumbers(numbers.stream()
			.map(StringPositiveNumber::from)
			.collect(Collectors.toList()));
	}

	public int sum() {
		int sum = 0;
		for (StringPositiveNumber number : numbers) {
			sum += number.parseInt();
		}
		return sum;
	}
}
