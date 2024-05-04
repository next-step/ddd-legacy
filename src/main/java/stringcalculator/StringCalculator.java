package stringcalculator;

import java.util.ArrayList;
import java.util.List;

public class StringCalculator {
	private final List<NonNegativeInteger> nonNegativeIntegers = new ArrayList<>();

	public StringCalculator(final String input) {
		this.nonNegativeIntegers.addAll(NonNegativeIntegerParser.parse(input));
	}

	public NonNegativeInteger calculate() {
		return nonNegativeIntegers.stream()
			.reduce(NonNegativeInteger.ZERO, NonNegativeInteger::add);
	}
}
