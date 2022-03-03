package calculation.convert;

public class StringSeparation {

	public static String[] getStrings(String formula) {
		return Delimiter.separateUsingDelimiter(formula);
	}
}
