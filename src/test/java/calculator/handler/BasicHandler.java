package calculator.handler;

import calculator.Numbers;
import calculator.separator.BasicSeparator;
import calculator.separator.Separator;

/**
 * <pre>
 * calculator.chain
 *      BasicHandler
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-03-03 오전 2:59
 */

public class BasicHandler implements CalculatorHandler {

    private CalculatorHandler handler;

    @Override
    public void nextHandler(CalculatorHandler handler) {
        this.handler = handler;
    }

    @Override
    public int calculate(String text) {
        return sum(text, new BasicSeparator());
    }

    private int sum(String text, Separator separator) {

        Numbers numbers = Numbers.of(separator.division(text));
        return numbers.sum();
    }
}
