package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

	private static final String DELIMITER_PATTERN = ",|:";

	public int calculate(final String input) {
		if (input == null || input.isEmpty()) {
			return 0;
		}

		final Matcher m = Pattern.compile("//(.)\n(.*)").matcher(input);
		if (m.find()) {
			String customDelimiter = m.group(1);

			return Arrays.stream(m.group(2).split(DELIMITER_PATTERN + "|" + customDelimiter))
				.map(InputNumber::from)
				.mapToInt(InputNumber::value)
				.sum();
		}

		return Arrays.stream(input.split(DELIMITER_PATTERN))
			.map(InputNumber::from)
			.mapToInt(InputNumber::value)
			.sum();
	}
}
