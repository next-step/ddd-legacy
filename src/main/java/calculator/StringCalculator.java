package calculator;

import java.util.stream.Stream;

public class StringCalculator {
	private static final int DEFAULT_VALUE = 0;
	private CalculatorDelimiter delimiter;

	public int add(String text) {
		if (isNullValue(text)) {
			return DEFAULT_VALUE;
		}
		String[] splittingText = getSplittingText(text);
		return Stream.of(splittingText)
			.map(CalculatorNumber::new)
			.mapToInt(CalculatorNumber::getNumber)
			.sum();
	}

	private boolean isNullValue(String text) {
		return text == null || text.isEmpty();
	}

	private String[] getSplittingText(String text) {
		delimiter = new CalculatorDelimiter(text);
		return delimiter.splittingText();
	}
}
