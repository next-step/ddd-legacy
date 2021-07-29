package study1;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

	private static final int DEFAULT_NUMBER = 0;
	private static final String DEFAULT_DELIM_REGEX = ",|:";
	private static final Pattern CUSTOM_DELIM_PATTERN = Pattern.compile("//(.)\n(.*)");

	public int add(final String text) {
		if (Objects.isNull(text)) {
			return DEFAULT_NUMBER;
		}

		if (text.isEmpty()) {
			return DEFAULT_NUMBER;
		}

		return summation(text);
	}

	private int summation(final String text) {
		Matcher m = CUSTOM_DELIM_PATTERN.matcher(text);
		if (m.find()) {
			String customDelimiter = m.group(1);
			String[] tokens= m.group(2).split(customDelimiter);
			return summation(tokens);
		}

		return summation(text.split(DEFAULT_DELIM_REGEX));
	}

	private static int summation(final String[] tokens) {
		return Arrays.stream(tokens)
			.map(StringCalculator::convert)
			.reduce(Integer::sum)
			.orElseThrow(RuntimeException::new);
	}

	private static int convert(String token) {
		final int number = Integer.parseInt(token);
		if (number < 0) {
			throw new RuntimeException();
		}

		return number;
	}
}
