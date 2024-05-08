package calculator;

import java.util.Arrays;

public class StringCalculator {

	private static final String DELIMITER_PATTERN = ",|:";

	public int calculate(final String input) {
		if (input == null || input.isEmpty()) {
			return 0;
		}

		return Arrays.stream(input.split(DELIMITER_PATTERN))
			.mapToInt(Integer::parseInt)
			.sum();
	}
}
