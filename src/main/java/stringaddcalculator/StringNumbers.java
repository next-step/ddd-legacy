package stringaddcalculator;

import java.util.List;
import java.util.stream.Collectors;

public class StringNumbers {

	private final List<StringNumber> numbers;

	private StringNumbers(List<StringNumber> numbers) {
		this.numbers = numbers;
	}

	public static StringNumbers from(List<StringNumber> numbers) {
		return new StringNumbers(numbers);
	}

	public static StringNumbers fromStringCollection(List<String> numbers) {
		List<StringNumber> StringNumberCollection = numbers.stream()
			.map(StringNumber::from)
			.collect(Collectors.toList());
		return new StringNumbers(StringNumberCollection);
	}

	public int sum() {
		int sum = 0;
		for (StringNumber number : numbers) {
			sum += number.parseInt();
		}
		return sum;
	}
}
