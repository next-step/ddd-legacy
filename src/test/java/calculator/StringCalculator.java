package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

	private static final String DELIMITER_PATTERN = ",|:";
	private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");

	public int calculate(final String input) {
		if (input == null || input.isEmpty()) {
			return 0;
		}

		final Matcher customDelimiterMatcher = CUSTOM_DELIMITER_PATTERN.matcher(input);
		if (customDelimiterMatcher.find()) {
			final String customDelimiter = customDelimiterMatcher.group(1);

			return Numbers.from(customDelimiterMatcher.group(2).split(DELIMITER_PATTERN + "|" + customDelimiter))
				.sum();
		}

		return Numbers.from(input.split(DELIMITER_PATTERN)).sum();
	}
}
