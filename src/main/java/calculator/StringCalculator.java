package calculator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringCalculator {
	private static final String DEFAULT_VALUE = "0";

	private final String text;
	private CalculatorDelimiter delimiter;
	private List<NumberForCalculator> numberForCalculators;

	public StringCalculator(String text) {
		this.text = text;
		delimiter = new CalculatorDelimiter(text);
		if (isNullValue()) {
			numberForCalculators = List.of(new NumberForCalculator(DEFAULT_VALUE));
			return;
		}
		numberForCalculators = Stream.of(delimiter.splittingText())
			.map(NumberForCalculator::new)
			.collect(Collectors.toList());
	}

	public int add() {
		return numberForCalculators.stream()
			.mapToInt(NumberForCalculator::getNumber)
			.sum();
	}

	private boolean isNullValue() {
		return text == null || text.isBlank();
	}

}
