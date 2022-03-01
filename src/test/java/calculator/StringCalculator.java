package calculator;

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

    private int ZERO = 0;

    public StringCalculator() {}

    public int add(String text) {
        if(isNullOrEmpty(text)) {
            return ZERO;
        }
        return -1;
    }

    public boolean isNullOrEmpty(final String text) {
        return isNull(text) || text.trim().isEmpty();
    }

    public boolean isNull(final String text) {
        return text == null;
    }
}
