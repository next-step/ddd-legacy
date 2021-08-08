package stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

	public int add(String inputNumber) {
		Numbers numbers = new Numbers(inputNumber);

		if (numbers.isNumberZero()) {
			return numbers.firstNumber();
		}

		if (numbers.isSummable()) {
			return numbers.sum();
		}

		return numbers.firstNumber();
	}

}
