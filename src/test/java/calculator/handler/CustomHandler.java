package calculator.handler;

import calculator.Numbers;
import calculator.separator.CustomSeparator;
import calculator.separator.Separator;

import java.util.regex.Matcher;

import static calculator.separator.CustomSeparator.CUSTOM_PATTERN;

/**
 * <pre>
 * calculator.chain
 *      CustomHandler
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-03-03 오전 2:48
 */

public class CustomHandler implements CalculatorHandler {

    private CalculatorHandler handler;

    @Override
    public void nextHandler(CalculatorHandler handler) {
        this.handler = handler;
    }

    @Override
    public int calculate(String text) {

        if (isCustomSeparator(text)) {
            return sum(text, new CustomSeparator());
        }

        return handler.calculate(text);
    }

    private int sum(String text, Separator separator) {
        Numbers numbers = Numbers.of(separator.division(text));
        return numbers.sum();
    }

    private boolean isCustomSeparator(final String text) {

        Matcher matcher = CUSTOM_PATTERN.matcher(text);
        return matcher.find();
    }
}
