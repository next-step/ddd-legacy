package calculator.handler;

import java.util.Objects;

/**
 * <pre>
 * calculator.chain
 *      EmptyHandler
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-03-03 오전 2:13
 */

public class EmptyHandler implements CalculatorHandler {

    private static final int EMPTY_TEXT_ZERO = 0;
    private CalculatorHandler handler;

    @Override
    public void nextHandler(CalculatorHandler handler) {
        this.handler = handler;
    }

    @Override
    public int calculate(final String text) {
        if (isNullOrEmpty(text)) {
            return EMPTY_TEXT_ZERO;
        }
        return handler.calculate(text);
    }

    private boolean isNullOrEmpty(final String text) {
        return isNull(text) || isEmpty(text);
    }

    private boolean isNull(final String text) {
        return Objects.isNull(text);
    }

    private boolean isEmpty(final String text) {
        return text.trim().isEmpty();
    }
}
