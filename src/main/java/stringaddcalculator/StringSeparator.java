package stringaddcalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringSeparator {

	private static final String DEFAULT_DELIMITER_REGEX = "[,:]";
	private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");

	private static final int DELIMITER_GROUP_NUMBER = 1;
	private static final int OPERAND_GROUP_NUMBER = 2;
	private final String target;

	private StringSeparator(String target) {
		this.target = target;
	}

	public static StringSeparator from(String target) {
		return new StringSeparator(target);
	}

	public List<String> split() {
		Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(target);
		if (matcher.find()) {
			return splitByCustomDelimiter(matcher);
		}
		return splitByDelimiter(target, DEFAULT_DELIMITER_REGEX);
	}

	private List<String> splitByDelimiter(String target, String defaultDelimiterRegex) {
		String[] stringArray = target.split(defaultDelimiterRegex);
		return new ArrayList<>(Arrays.asList(stringArray));
	}

	private List<String> splitByCustomDelimiter(Matcher matcher) {
		String customDelimiter = matcher.group(DELIMITER_GROUP_NUMBER);
		return splitByDelimiter(matcher.group(OPERAND_GROUP_NUMBER), customDelimiter);
	}
}
