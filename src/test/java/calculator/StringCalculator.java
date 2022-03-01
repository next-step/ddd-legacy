package calculator;

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
    private String COMMA = ",";
    private String COLON = ":";

    public StringCalculator() {
    }

    public int add(String text) {
        if (isNullOrEmpty(text)) {
            return EMPTY_TEXT_ZERO;
        }

        if (isSingleNumber(text)) {
            return Integer.parseInt(text);
        }

        return division(text);
    }

    private int division(String text) {
        Numbers numbers = Numbers.of(
            text.split(separator(COMMA, COLON))
        );
        return numbers.sum();
    }

    private String separator(String... separator) {
        return Arrays.stream(separator).collect(Collectors.joining("|"));
    }
}
