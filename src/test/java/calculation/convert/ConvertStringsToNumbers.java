package calculation.convert;

import calculation.calculator.CalculationResult;
import calculation.number.Number;
import calculation.number.Numbers;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static calculation.ErrorMessage.THIS_DELIMITER_ARE_NOT_ALLOWED;
import static calculation.convert.StringSeparation.*;

public class ConvertStringsToNumbers {
	private final CalculationResult result;

	public int getResult() {
		return result.getResult();
	}

	public ConvertStringsToNumbers(String formula) {
		result = this.deriveResults(formula);
	}

	public CalculationResult deriveResults(String formula) {
		if (isNull(formula) || isEmpty(formula)) {
			return new CalculationResult();
		}
		String[] strings = getStrings(formula);
		Numbers numbers = new Numbers(Arrays.stream(strings).map(this::convertStringToNumber).collect(Collectors.toList()));
		return new CalculationResult(numbers.sum());
	}

	private Number convertStringToNumber(String string) {
		if (isNull(string) || isEmpty(string)) {
			return new Number();
		}
		try {
			final int parseInt = Integer.parseInt(string);
			return new Number(parseInt);
		} catch (NumberFormatException e) {
			throw new RuntimeException(THIS_DELIMITER_ARE_NOT_ALLOWED + " : " + string);
		}
	}

	private boolean isNull(String string) {
		return Objects.isNull(string);
	}

	private boolean isEmpty(String string) {
		return string.trim().isEmpty();
	}
}
