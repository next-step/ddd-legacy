package calculator;

import static calculator.CalculratorValidation.*;

/**
 * <pre>
 * calculator
 *      CalculratorText
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-03-02 오전 12:35
 */

public class StringCalculator {

    private int EMPTY_TEXT_ZERO = 0;

    public StringCalculator() {}

    public int add(String text) {
        if(isNullOrEmpty(text)) {
            return EMPTY_TEXT_ZERO;
        }

        if(isSingleNumber(text)) {
            return Integer.parseInt(text);
        }

        return -1;
    }
}
