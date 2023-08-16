package calculator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class StringCalculator {
	private static final int ZERO_OF_STRING_CALCULATOR = 0;
	private static final List<String> DELIMITER_FOR_ADD = List.of(",", ":");
	private static final Pattern pattern = Pattern.compile("//(.)\n(.*)");

	public int add(String text) {
		if (isNullValue(text)) {
			return ZERO_OF_STRING_CALCULATOR;
		}
		if (isSingleText(text)) {
			getNumeric(text);
		}

		return Stream.of(splittingText(text))
			.mapToInt(this::getNumeric)
			.sum();
	}

	private boolean isNullValue(String text) {
		return text == null || text.isEmpty();
	}

	private boolean isSingleText(String text) {
		return text.length() == 1;
	}

	private int getNumeric(String text) {
		try {
			return Integer.parseInt(text);
		} catch (NumberFormatException e) {
			throw new RuntimeException("숫자가 아닌 값이 존재 합니다.");
		}
	}

	private String[] splittingText(String text) {
		Matcher matcher = getCustomDelimiterMatcher(text);
		if (matcher.find()) {
			return matcher.group(2)
				.split(matcher.group(1));
		}
		String regex = String.format("[%s]"
			, String.join("", DELIMITER_FOR_ADD));
		return text.split(regex);
	}

	private Matcher getCustomDelimiterMatcher(String text) {
		return pattern.matcher(text);
	}
}
