package calculator.handler;

import calculator.Number;

/**
 * <pre>
 * calculator.chain
 *      SingleNumberHandler
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-03-03 오전 2:47
 */

public class SingleNumberHandler implements CalculatorHandler {

    private CalculatorHandler handler;

    @Override
    public void nextHandler(CalculatorHandler handler) {
        this.handler = handler;
    }

    @Override
    public int calculate(final String text) {
        if (isSingleNumber(text)) {
            Number number = new Number(text);
            return number.value();
        }
        return handler.calculate(text);
    }

    private boolean isSingleNumber(final String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
