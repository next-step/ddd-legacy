package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class StringCalculator {

	private static final Pattern pattern = Pattern.compile("//(.)\n(.*)");

	public int add(String text) {
		if (text == null || text.isEmpty()) {
			return 0;
		}

		return Stream.of(parseNumbers(text))
			.map(Integer::parseInt)
			.map(PositiveNumber::new)
			.reduce(new PositiveNumber(0), PositiveNumber::plus)
			.value();
	}

	private String[] parseNumbers(String text) {
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			String customDelimiter = matcher.group(1);
			return matcher.group(2).split(customDelimiter);
		}

		return text.split("[,:]");
	}
}
