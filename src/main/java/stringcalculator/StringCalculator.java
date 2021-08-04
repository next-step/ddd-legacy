package stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

	private static final Pattern pattern = Pattern.compile("//(.)\n(.*)");

	private final int ZERO_NUMBER = 0;
	private final int LENGTH_OF_NUMBER_FOR_SUM = 2;

	public int add(String inputNumber) {

		if (inputNumber == null || inputNumber.trim().isEmpty()) {
			return ZERO_NUMBER;
		}

		Matcher m = pattern.matcher(inputNumber);

		if (m.find()) {
			String customDelimiter = m.group(1);
			String[] tokens= m.group(2).split(customDelimiter);
			return getSum(tokens);
		}

		String[] numbers = inputNumber.split(",|:");

		if (numbers.length >= LENGTH_OF_NUMBER_FOR_SUM) {
			return getSum(numbers);
		}

		return convertNumberToInteger(inputNumber);
	}

	private int getSum(String[] tokens) {
		return Arrays.stream(tokens).mapToInt(i -> convertNumberToInteger(i)).sum();
	}

	private Integer convertNumberToInteger(String inputNumber) {
		if (Integer.parseInt(inputNumber) < ZERO_NUMBER) {
			throw new RuntimeException();
		}
		return Integer.parseInt(inputNumber);
	}
}
