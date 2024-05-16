package calculator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Splitter {
	private static final List<String> DEFAULT_DELIMITERS = List.of(",", ":");
	private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
	private static final int CUSTOM_DELIMITER_GROUP = 1;
	private static final int TARGET_GROUP = 2;
	private static final String REGEX_JOIN_DELIMITER = "|";

	private Splitter() {
	}

	public static String[] splitText(String text) {
		List<String> delimiters = new ArrayList<>(DEFAULT_DELIMITERS);
		Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);
		if (matcher.find()) {
			String customDelimiter = matcher.group(CUSTOM_DELIMITER_GROUP);
			delimiters.add(customDelimiter);
			text = matcher.group(TARGET_GROUP);
		}
		String regex = String.join(REGEX_JOIN_DELIMITER, delimiters);
		return text.split(regex);
	}
}
