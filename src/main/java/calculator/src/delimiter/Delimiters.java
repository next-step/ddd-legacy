package calculator.src.delimiter;

import static java.util.stream.Collectors.toUnmodifiableList;

import calculator.src.CalculatorString;
import java.util.Collection;
import java.util.List;

public class Delimiters {

	private final List<Delimiter> values;

	public Delimiters(List<Delimiter> values) {
		this.values = values;
	}

	public List<CalculatorString> tokenize(CalculatorString calculatorString) {
		List<CalculatorString> tokenizedString = tokenizingByCustomDelimiter(calculatorString);

		return tokenizedString.stream()
			.map(v -> tokenizingByTokenIndex(v, 0))
			.flatMap(Collection::stream)
			.collect(toUnmodifiableList());
	}

	private List<CalculatorString> tokenizingByTokenIndex(CalculatorString calculatorString, int index) {
		if (values.size() <= index) {
			return List.of(calculatorString);
		}

		return calculatorString.tokenizingBy(values.get(index)).stream()
			.map(v -> tokenizingByTokenIndex(v, index + 1))
			.flatMap(Collection::stream)
			.collect(toUnmodifiableList());
	}

	private List<CalculatorString> tokenizingByCustomDelimiter(CalculatorString calculatorString) {
		return calculatorString.extractCustomDelimiter()
			.map(calculatorString::tokenizingBy)
			.orElse(List.of(calculatorString));
	}
}
