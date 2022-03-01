package calculator;

public class CalculatorUtil {

    public static boolean isNullOrEmpty(String text) {
        if(text == null || "".equals(text) || " ".equals(text) ) {
            return true;
        }
        return false;
    }

    public static boolean isNumeric(String text) {
        try {
            Double d = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static int toInt(String text) {
        return Integer.parseInt(text);
    }
}
