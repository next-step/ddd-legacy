package calculator.handler;

import calculator.CalculratorValidation;

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

        if (CalculratorValidation.isNullOrEmpty(text)) {
            return EMPTY_TEXT_ZERO;
        }

        return handler.calculate(text);
    }
}
