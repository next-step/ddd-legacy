package calculator;

import calculator.separator.BasicSeparator;
import calculator.separator.CustomSeparator;
import calculator.separator.Separator;

import javax.persistence.Basic;
import java.util.Arrays;
import java.util.stream.Collectors;

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
        if (isNullOrEmpty(text)) {
            return EMPTY_TEXT_ZERO;
        }

        if (isSingleNumber(text)) {
            CalculratorValidation.convertValidation(text);
            return Integer.parseInt(text);
        }

        if(isCustomSeparator(text)) {
            return sum(text, new CustomSeparator());
        }

        return sum(text, new BasicSeparator());
    }

    private int sum(String text, Separator separator) {
        Numbers numbers = Numbers.of(separator.division(text));
        return numbers.sum();
    }
}
