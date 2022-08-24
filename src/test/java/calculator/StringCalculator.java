package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

	private static final String DELIMITER = "[,:]";
	private static final Pattern CUSTOM_DELIMITER = Pattern.compile("//(.)\\n(.*)");

	private final StringCalculationSpec spec;

	public StringCalculator(StringCalculationSpec spec) {
		this.spec = spec;
	}

	public int add(String input) {
		if (spec.isNullOrEmpty(input)) {
			return 0;
		}

		if (spec.isSingleNumber(input)) {
			return Integer.parseInt(input);
		}

		Matcher matcher = CUSTOM_DELIMITER.matcher(input);
		if (matcher.find()) {
			return sum(getDelimitedTokensByMatcher(matcher));
		}

		return sum(input.split(DELIMITER));
	}

	private String[] getDelimitedTokensByMatcher(Matcher matcher) {
		String customDelimiter = matcher.group(1);
		String tokenToBeDelimited = matcher.group(2);
		return tokenToBeDelimited.split(customDelimiter);
	}

	private int sum(String[] tokens) {
		spec.checkNumbers(tokens);

		return Arrays.stream(tokens)
			.mapToInt(Integer::parseInt)
			.sum();
	}
}
