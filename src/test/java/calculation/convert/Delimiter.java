package calculation.convert;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Delimiter {
	private final static String CUSTOM_DELIMITER = "[,:]";
	private final static String DEFAULT_DELIMITER = "//(.)\n(.*)";

	private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");

	public static String[] separateUsingDelimiter(String formula) {
		Matcher m = CUSTOM_DELIMITER_PATTERN.matcher(formula);
		if (m.find()) {
			String customDelimiter = m.group(1);
			return m.group(2).split(customDelimiter);
		}

		return formula.split(CUSTOM_DELIMITER);
	}


}
