package calculation.convert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomDelimiter {
	private static final String THE_EXPRESSION_IS_UNKNOWN = "표현식을 알 수 없습니다.";
	private static final String CUSTOM_DELIMITER = "//(.)\n(.*)";
	private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile(CUSTOM_DELIMITER);

	public static String[] convertFormula(String text) {
		Matcher m = CUSTOM_DELIMITER_PATTERN.matcher(text);
		if (m.find()) {
			String customDelimiter = m.group(1);
			return m.group(2).split(customDelimiter);
		}
		throw new RuntimeException(THE_EXPRESSION_IS_UNKNOWN);
	}

}
