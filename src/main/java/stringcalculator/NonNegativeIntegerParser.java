package stringcalculator;

import java.math.BigInteger;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class NonNegativeIntegerParser {
	private static final int DELIMITER_GROUP_INDEX = 1;
	private static final int OPERAND_GROUP_INDEX = 2;

	private static final Pattern INPUT_PATTERN = Pattern.compile("//(.)\n(.*)");

	private static final String DELIMITER_REGEX = ",|:";

	private NonNegativeIntegerParser() {
	}

	public static List<NonNegativeInteger> parse(String input) {
		if (input == null || input.isBlank()) {
			return List.of(NonNegativeInteger.valueOf(BigInteger.ZERO));
		}

		return Stream.of(extractTokens(input))
			.map(NonNegativeInteger::valueOf)
			.toList();
	}

	private static String[] extractTokens(final String input) {
		Matcher matcher = INPUT_PATTERN.matcher(input);

		if (matcher.find()) {
			String customDelimiter = matcher.group(DELIMITER_GROUP_INDEX);
			return matcher.group(OPERAND_GROUP_INDEX).split(Pattern.quote(customDelimiter));
		}

		return input.split(DELIMITER_REGEX);
	}
}
