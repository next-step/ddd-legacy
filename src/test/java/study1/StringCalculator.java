package study1;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.StringUtils;

public class StringCalculator {

	private static final int DEFAULT_NUMBER = 0;
	private static final String DEFAULT_DELIM_REGEX = ",|:";
	private static final Pattern CUSTOM_DELIM_PATTERN = Pattern.compile("//(.)\n(.*)");
	private static final int DELIMITER_INDEX = 1;
	private static final int CONTENT_INDEX = 2;

	public int add(final String text) {
		if (!StringUtils.hasText(text)) {
			return DEFAULT_NUMBER;
		}

		return summation(text).value();
	}

	private static PositiveNumber summation(final String text) {
		Matcher m = CUSTOM_DELIM_PATTERN.matcher(text);
		if (m.find()) {
			String customDelimiter = m.group(DELIMITER_INDEX);
			String[] tokens= m.group(CONTENT_INDEX).split(customDelimiter);
			return summation(tokens);
		}

		return summation(text.split(DEFAULT_DELIM_REGEX));
	}

	private static PositiveNumber summation(final String[] tokens) {
		return Arrays.stream(tokens)
			.map(PositiveNumber::valueOf)
			.reduce(PositiveNumber::add)
			.orElseThrow(RuntimeException::new);
	}
}
