package stringcalculator;

import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;

public class StringCalculator {
    private static final List<String> DEFAULT_DELIMITER = Arrays.asList(",", ":");

    private BinaryOperator<Integer> addOperator;

    public StringCalculator() {
        addOperator = (a, b) -> a + b;
    }

    public int add(String text) {
        if (isEmpty(text)) {
            return new Operand().getValue();
        }
        Operands operands = Operands.of(makeSplitter(text).split(text));
        return operands.calculate(addOperator);
    }

    private boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }

    private StringSplitter makeSplitter(String text) {
        StringSplitter splitter = new StringSplitter(DEFAULT_DELIMITER);
        return splitter.matchCustom(text);
    }
}
