package calculator;

import calculator.separator.CustomSeparator;
import org.thymeleaf.expression.Numbers;

import java.util.Objects;
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
        return Objects.isNull(text);
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

    public static void numberValidation(int number) {
        if(number >= 0) {
            return;
        }

        throw new RuntimeException("문자열 계산기에는 음수를 사용할 수 없습니다.");
    }

    public static void convertValidation(String stringNumber) {
        try {
            Integer number = Integer.valueOf(stringNumber);
            numberValidation(number);
        }catch (NumberFormatException e) {
            throw new RuntimeException("문자열 계산기에 숫자 이외의 값을 사용할 수 없습니다.");
        }
    }
}
