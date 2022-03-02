package calculator.handler;

import calculator.CalculratorValidation;

import static calculator.CalculratorValidation.*;

/**
 * <pre>
 * calculator.chain
 *      SingleNumberHandler
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-03-03 오전 2:47
 */

public class SingleNumberHandler implements CalculatorHandler{

    private CalculatorHandler handler;

    @Override
    public void nextHandler(CalculatorHandler handler) {
        this.handler = handler;
    }

    @Override
    public int calculate(String text) {

        if(isSingleNumber(text)) {
            CalculratorValidation.validate(text);
            return Integer.parseInt(text);
        }

        return handler.calculate(text);
    }
}
