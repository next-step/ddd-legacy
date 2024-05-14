package calculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
	private static final Pattern PATTERN = Pattern.compile("//(.)\n(.*)");
	private static final int CUSTOM_DELIMITER_GROUP = 1;
	private static final int TARGET_GROUP = 2;

	public int add(String text) {
		if (text == null || text.isEmpty()) {
			return 0;
		}
		String target = text;
		Delimiters delimiters = new Delimiters();
		Matcher matcher = PATTERN.matcher(text);
		if (matcher.find()) {
			String customDelimiter = matcher.group(CUSTOM_DELIMITER_GROUP);
			delimiters.add(customDelimiter);
			target = matcher.group(TARGET_GROUP);
		}
		String regex = String.join("|", delimiters.getDelimiters());
		return Arrays.stream(target.split(regex))
			.mapToInt(this::parseToPositiveNumber)
			.sum();
	}

	private int parseToPositiveNumber(String text) {
		return new PositiveNumber(text).getNumber();
	}
}

class Delimiters {
	private static final List<String> DEFAULT_DELIMITERS = List.of(",", ":");
	private final List<String> delimiters = new ArrayList<>(DEFAULT_DELIMITERS);

	public List<String> getDelimiters() {
		return delimiters;
	}

	public void add(String customDelimiter) {
		delimiters.add(customDelimiter);
	}
}

class PositiveNumber {
	private final int number;

	public PositiveNumber(String text) {
		if (Integer.parseInt(text) < 0) {
			throw new RuntimeException();
		}
		number = Integer.parseInt(text);
	}

	public int getNumber() {
		return number;
	}
}
