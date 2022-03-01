package calculator;

public class CalculatorUtil {

    public static boolean isNullOrEmpty(String text) {
        return text == null || "".equals(text) || " ".equals(text);
    }

    public static int toInt(String text) {
        return Integer.parseInt(text);
    }

    public static boolean isNumeric(String text) {
        try {
            Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
