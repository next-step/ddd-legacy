package calculator;

import org.thymeleaf.expression.Numbers;

/**
 * <pre>
 * calculator
 *      CalculratorValidation
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-03-02 오전 1:34
 */

public class CalculratorValidation {

    private static final int ZERO = 0;

    public static boolean isNullOrEmpty(final String text) {
        return isNull(text) || text.trim().isEmpty();
    }

    public static boolean isNull(final String text) {
        return text == null;
    }

    public static boolean isSingleNumber(String text) {
        try {
            Integer.parseInt(text);
            return true;
        }catch (NumberFormatException e) {
            return false;
        }
    }
}
