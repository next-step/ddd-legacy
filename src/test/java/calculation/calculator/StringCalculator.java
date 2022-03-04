package calculation.calculator;

import calculation.convert.StringSeparator;

public class StringCalculator {

	public static int add(String formula) {
		final StringSeparator converter = new StringSeparator();
		return converter.getResult(formula);
	}
}
