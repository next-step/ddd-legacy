package stringcalculator;

import java.util.Arrays;

public class StringCalculator {

	public int add(String inputNumber) {

		if (inputNumber == null || inputNumber.trim().isEmpty()) {
			return 0;
		}

		String[] numbers = inputNumber.split(",|:");

		if (numbers.length > 1) {
			return Arrays.stream(numbers).mapToInt(i -> Integer.parseInt(i)).sum();
		}

		return Integer.parseInt(inputNumber);
	}
}
