package stringcalculator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class StringCalculator {
	private final List<NonNegativeInteger> nonNegativeIntegers = new ArrayList<>();

	public StringCalculator(final String input) {
		this.nonNegativeIntegers.addAll(NonNegativeIntegerParser.parse(input));
	}

	public BigInteger calculate() {
		return nonNegativeIntegers.stream()
			.map(NonNegativeInteger::value)
			.reduce(BigInteger.ZERO, BigInteger::add);
	}
}
