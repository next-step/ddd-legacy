package stringcalculator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class StringCalculator {
	private final OperandParser parser = new OperandParser();

	private final List<NonNegativeInteger> nonNegativeIntegers = new ArrayList<>();

	public StringCalculator(final String input) {
		this.nonNegativeIntegers.addAll(parser.parse(input));
	}

	public BigInteger calculate() {
		return nonNegativeIntegers.stream()
			.map(NonNegativeInteger::value)
			.reduce(BigInteger.ZERO, BigInteger::add);
	}
}
