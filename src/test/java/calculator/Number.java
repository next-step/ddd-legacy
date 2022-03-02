package calculator;

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

    public Number(int value) {

        this.value = value;
    }

    public static Number of(String value) {

        CalculratorValidation.validate(value);
        return new Number(Integer.parseInt(value));
    }

    public int value() {

        return this.value;
    }
}
