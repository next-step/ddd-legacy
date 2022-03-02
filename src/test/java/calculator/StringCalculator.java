package calculator;

import calculator.chain.*;
import calculator.separator.BasicSeparator;
import calculator.separator.CustomSeparator;
import calculator.separator.Separator;

import javax.persistence.Basic;
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

    private CalculatorHandler rootHandler;
    public StringCalculator() {
        initHandler();
    }

    private void initHandler() {
        rootHandler = new EmptyHandler();
        CalculatorHandler singleNumberHandler = new SingleNumberHandler();
        CalculatorHandler customHandler = new CustomHandler();
        CalculatorHandler basicHandler = new BasicHandler();

        rootHandler.nextHandler(singleNumberHandler);
        singleNumberHandler.nextHandler(customHandler);
        customHandler.nextHandler(basicHandler);
    }

    public int add(String text) {
        return rootHandler.calculate(text);
    }

    private int sum(String text, Separator separator) {

        Numbers numbers = Numbers.of(separator.division(text));
        return numbers.sum();
    }
}
