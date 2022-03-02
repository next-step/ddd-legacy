package calculator;

import java.util.Objects;
import java.util.regex.Matcher;

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
        return isNull(text) || isEmpty(text);
    }

    private static boolean isNull(final String text) {
        return Objects.isNull(text);
    }

    private static boolean isEmpty(final String text) {
        return text.trim().isEmpty();
    }

    public static boolean isSingleNumber(final String text) {
        try {
            Integer.parseInt(text);
            return true;
        }catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isCustomSeparator(final String text) {
        Matcher matcher = CUSTOM_PATTERN.matcher(text);
        return matcher.find();
    }

    public static void numberValidation(final int number) {
        if(number >= 0) {
            return;
        }

        throw new RuntimeException("문자열 계산기에는 음수를 사용할 수 없습니다.");
    }

    public static void convertValidation(final String stringNumber) {
        try {
            Integer number = Integer.valueOf(stringNumber);
            numberValidation(number);
        }catch (NumberFormatException e) {
            throw new RuntimeException("문자열 계산기에 숫자 이외의 값을 사용할 수 없습니다.");
        }
    }
}
