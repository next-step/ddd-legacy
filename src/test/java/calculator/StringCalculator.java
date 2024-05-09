package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

	private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
	private static final Splitter SPLITTER = Splitter.from(",", ":");

	public int calculate(final String input) {
		if (input == null || input.isEmpty()) {
			return 0;
		}

		final Matcher customDelimiterMatcher = CUSTOM_DELIMITER_PATTERN.matcher(input);
		if (customDelimiterMatcher.find()) {
			final String customDelimiter = customDelimiterMatcher.group(1);

			return Numbers.from(SPLITTER.addDelimiter(customDelimiter).split(customDelimiterMatcher.group(2)))
				.sum();
		}

		return Numbers.from(SPLITTER.split(input)).sum();
	}
}
