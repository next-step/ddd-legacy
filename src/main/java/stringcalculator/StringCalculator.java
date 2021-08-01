package stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

	public int add(String inputNumber) {

		if (inputNumber == null || inputNumber.trim().isEmpty()) {
			return 0;
		}

		Matcher m = Pattern.compile("//(.)\n(.*)").matcher(inputNumber);
		if (m.find()) {
			String customDelimiter = m.group(1);
			String[] tokens= m.group(2).split(customDelimiter);
			return Arrays.stream(tokens).mapToInt(i -> {
				int number = Integer.parseInt(i);
				if (number < 0) {
					throw new RuntimeException();
				}
				return number;
			}).sum();
		}

		String[] numbers = inputNumber.split(",|:");

		if (numbers.length > 1) {
			return Arrays.stream(numbers).mapToInt(i -> {
				int number = Integer.parseInt(i);
				if (number < 0) {
					throw new RuntimeException();
				}
				return number;
			}).sum();
		}

		if (Integer.parseInt(inputNumber) < 0) {
			throw new RuntimeException();
		}

		return Integer.parseInt(inputNumber);
	}
}
