package calculation.convert;

public class DefaultDelimiter {
	final static String COLONS_REGEX = "[,:]";

	public static String[] convertFormula(String text) {
		return text.split(COLONS_REGEX);
	}
}
