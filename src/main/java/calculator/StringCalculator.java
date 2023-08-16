package calculator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringCalculator {
	private static final int DEFAULT_VALUE = 0;
	private CalculatorDelimiter delimiter;
	private List<NumberForCalculator> numberForCalculators;

	public int add(String text) {
		if (isNullValue(text)) {
			return DEFAULT_VALUE;
		}
		String[] splittingText = getSplittingText(text);
		numberForCalculators = Stream.of(splittingText)
			.map(NumberForCalculator::new)
			.collect(Collectors.toList());

		return numberForCalculators.stream()
			.mapToInt(NumberForCalculator::getNumber)
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
