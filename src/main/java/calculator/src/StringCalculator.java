package calculator.src;

import static java.util.stream.Collectors.toUnmodifiableList;

import calculator.src.delimiter.Delimiters;
import java.util.List;

public class StringCalculator {

	private final Delimiters delimiters;

	public StringCalculator(Delimiters delimiters) {
		this.delimiters = delimiters;
	}

	public int add(String text) {
		List<CalculatorString> calculatorStrings = delimiters.tokenize(new CalculatorString(text));
		List<PositiveNumber> positiveNumbers = toPositiveNumbers(calculatorStrings);
		PositiveNumber positiveNumber = addAll(positiveNumbers);

		return positiveNumber.intValue();
	}

	private List<PositiveNumber> toPositiveNumbers(List<CalculatorString> calculatorStrings) {
		return calculatorStrings.stream()
			.map(CalculatorString::toPositiveNumber)
			.collect(toUnmodifiableList());
	}

	private PositiveNumber addAll(List<PositiveNumber> positiveNumbers) {
		return positiveNumbers.stream()
			.reduce(PositiveNumber.ZERO, PositiveNumber::sum);
	}
}
