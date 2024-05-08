package calculator;

import java.util.Arrays;

public class StringCalculator {

	private static final String DELIMITER_PATTERN = ",|:";

	public int calculate(final String input) {
		if (input == null || input.isEmpty()) {
			return 0;
		}

		Arrays.stream(input.split(DELIMITER_PATTERN))
			.map(Integer::parseInt)
			.filter(n -> n < 0)
			.findFirst()
			.ifPresent(n -> {
				throw new IllegalArgumentException("Negative number not allowed: " + n);
			});

		return Arrays.stream(input.split(DELIMITER_PATTERN))
			.mapToInt(Integer::parseInt)
			.sum();
	}
}
