package calculation.convert;

import calculation.number.Number;
import calculation.number.Numbers;

import static calculation.calculator.CalculatorCheckValidation.*;

public class ConvertStringsToNumbers {

	public static Numbers convertToNumbers(String text) {
		Numbers numbers = new Numbers();
		if (isNull(text) || isEmpty(text)) {
			numbers.addZero();
			return numbers;
		}
		if (isNum(text)) {
			Number number = Number.convert(text);
			numbers.getNumbers().add(number);
			return numbers;
		}
		if (isContainsColons(text)) {
			return numbers.convertStringsToIntegers(DefaultDelimiter.convertFormula(text));
		}
		return numbers.convertStringsToIntegers(CustomDelimiter.convertFormula(text));
	}
}
