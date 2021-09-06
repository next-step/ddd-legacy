package stringcalculator;

import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.function.BinaryOperator;

public class StringCalculator {
    private static final BinaryOperator<Integer> ADD_OPERATOR = (a, b) -> a + b;;

    public int add(String text) {
        if (isEmpty(text)) {
            return new Operand().getValue();
        }
        StringSplitter splitter = new StringSplitter();
        Operands operands = Operands.of(splitter.split(text));
        return operands.calculate(ADD_OPERATOR);
    }

    private boolean isEmpty(String text) {
        return Objects.isNull(text) || !StringUtils.hasText(text);
    }
}
