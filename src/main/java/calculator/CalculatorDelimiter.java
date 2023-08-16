package calculator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalculatorDelimiter {
	private static final List<String> DELIMITER_OF_STANDARD = List.of(",", ":");
	private static final Pattern pattern = Pattern.compile("//(.)\n(.*)");

	private String text;

	public CalculatorDelimiter(String text) {
		this.text = text;
	}

	public String[] splittingText() {
		Matcher matcher = getCustomDelimiterMatcher();

		if (matcher.find()) {
			return matcher.group(2)
				.split(matcher.group(1));
		}
		return text.split(getStandardDelimiter());
	}

	private String getStandardDelimiter() {
		return String.format("[%s]"
			, String.join("", DELIMITER_OF_STANDARD));
	}

	private Matcher getCustomDelimiterMatcher() {
		return pattern.matcher(text);
	}
}
