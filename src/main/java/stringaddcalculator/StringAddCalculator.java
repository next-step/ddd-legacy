package stringaddcalculator;

import java.util.List;

public class StringAddCalculator {

	private StringAddCalculator() {
		throw new AssertionError();
	}

	public static int calculate(String input) {
		if (input == null || input.isEmpty()) {
			return 0;
		}
		List<String> split = StringSeparator.from(input).split();
		StringPositiveNumbers stringPositiveNumbers = StringPositiveNumbers.fromStringCollection(split);
		return stringPositiveNumbers.sum();
	}
}
