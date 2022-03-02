package calculation.calculator;


import calculation.convert.ConvertStringsToNumbers;
import calculation.number.Numbers;

public class StringCalculator {
	public int add(String text) {
		final Numbers numbers = ConvertStringsToNumbers.convertToNumbers(text);
		return numbers.sum();
	}
}
