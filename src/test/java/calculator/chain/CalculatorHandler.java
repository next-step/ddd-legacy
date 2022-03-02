package calculator.chain;

/**
 * <pre>
 * calculator.chain
 *      CalculatorHandler
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-03-03 오전 2:01
 */

public interface CalculatorHandler {

    void nextHandler(CalculatorHandler handler);
    int calculate(String text);
}
