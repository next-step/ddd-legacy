package calculator;

public class NumberUtil {

    private NumberUtil() {
    }

    public static boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
