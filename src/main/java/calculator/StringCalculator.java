package calculator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringCalculator {
	private static final int DEFAULT_VALUE = 0;

	private final String text;
	private CalculatorDelimiter delimiter;
	private List<NumberForCalculator> numberForCalculators;

	public StringCalculator(String text) {
		this.text = text;
	}

	public int add() {
		if (isNullValue()) {
			return DEFAULT_VALUE;
		}
		String[] splittingText = getSplittingText();
		numberForCalculators = Stream.of(splittingText)
			.map(NumberForCalculator::new)
			.collect(Collectors.toList());

		return numberForCalculators.stream()
			.mapToInt(NumberForCalculator::getNumber)
			.sum();
	}

	private boolean isNullValue() {
		return text == null || text.isEmpty();
	}

	private String[] getSplittingText() {
		delimiter = new CalculatorDelimiter(text);
		return delimiter.splittingText();
	}
}
