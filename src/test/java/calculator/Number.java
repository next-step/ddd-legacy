package calculator;

import com.sun.javafx.css.CalculatedValue;
import org.junit.jupiter.api.Test;

/**
 * <pre>
 * calculator
 *      Number
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-03-03 오전 1:18
 */

public class Number {
    private final int value;

    public Number(String value) {
        validate(value);
        this.value = Integer.parseInt(value);
    }

    public int value() {
        return this.value;
    }

    private void validateNumber(final int number) {
        if (number < 0) {
            throw new RuntimeException("문자열 계산기에는 음수를 사용할 수 없습니다.");
        }
    }

    private void validate(final String stringNumber) {
        try {
            Integer number = Integer.valueOf(stringNumber);
            validateNumber(number);
        } catch (NumberFormatException e) {

            throw new RuntimeException("문자열 계산기에 숫자 이외의 값을 사용할 수 없습니다.");
        }
    }
}
