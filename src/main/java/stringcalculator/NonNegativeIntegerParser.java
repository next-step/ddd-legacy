package stringcalculator;

import java.math.BigInteger;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class NonNegativeIntegerParser {
	private static final Pattern INPUT_PATTERN = Pattern.compile("//(.)\n(.*)");

	private static final String DELIMITER_REGEX = ",|:";

	private NonNegativeIntegerParser() {
	}

	public static List<NonNegativeInteger> parse(String input) {
		if (input == null || input.isBlank()) {
			return List.of(NonNegativeInteger.from(BigInteger.ZERO));
		}

		return Stream.of(extractTokens(input))
			.map(NonNegativeInteger::from)
			.toList();
	}

	private static String[] extractTokens(final String input) {
		Matcher matcher = INPUT_PATTERN.matcher(input);

		if (matcher.find()) {
			String customDelimiter = matcher.group(1);
			return matcher.group(2).split(Pattern.quote(customDelimiter));
		}

		return input.split(DELIMITER_REGEX);
	}
}
