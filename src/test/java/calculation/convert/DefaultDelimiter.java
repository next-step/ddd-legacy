package calculation.convert;

import java.util.regex.Matcher;

public class DefaultDelimiter {
	final static String COLONS_REGEX = "[,:]";

	public static String[] convertFormula(String text) {
		return text.split(COLONS_REGEX);
	}
}
