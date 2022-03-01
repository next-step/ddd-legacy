package calculator;

import calculator.separator.CustomSeparator;
import org.thymeleaf.expression.Numbers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static calculator.separator.CustomSeparator.*;

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

    public static boolean isCustomSeparator(String text) {
        Matcher matcher = CUSTOM_PATTERN.matcher(text);
        return matcher.find();
    }
}
