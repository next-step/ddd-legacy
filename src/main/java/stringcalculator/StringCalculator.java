package stringcalculator;

public class StringCalculator {
	private final String input;

	public StringCalculator(final String input) {
		this.input = input;
	}

	public PositiveInteger calculate() {
		return PositiveIntegerParser.parse(input)
			.stream()
			.reduce(PositiveInteger.ZERO, PositiveInteger::add);
	}
}
