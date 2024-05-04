package stringcalculator;

public class StringCalculator {
	private final String input;

	public StringCalculator(final String input) {
		this.input = input;
	}

	public NonNegativeInteger calculate() {
		return NonNegativeIntegerParser.parse(input)
			.stream()
			.reduce(NonNegativeInteger.ZERO, NonNegativeInteger::add);
	}
}
