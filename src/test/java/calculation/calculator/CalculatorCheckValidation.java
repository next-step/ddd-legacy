package calculation.calculator;

public class CalculatorCheckValidation {

	public static boolean isNull(String text) {
		return text == null;
	}

	public static boolean isEmpty(String text) {
		return text.trim().isEmpty();
	}

	public static boolean isContainsColons(String text) {
		return text.contains(":") || text.contains(",");
	}

	public static boolean isNum(String text) {
		try {
			Integer.parseInt(text);
			return true;
		}catch (NumberFormatException e) {
			return false;
		}
	}

}
