package stringcalculator;

import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;

import static java.util.stream.Collectors.toList;

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
        List<Operand> operands = splitToOperand(text, makeSplitter(text));
        return calculate(operands).getValue();
    }

    private boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }

    private StringSplitter makeSplitter(String text) {
        StringSplitter splitter = new StringSplitter(DEFAULT_DELIMITER);
        return splitter.matchCustom(text);
    }

    private List<Operand> splitToOperand(String text, StringSplitter splitter) {
        return splitter.split(text).stream()
                .map(Operand::new)
                .collect(toList());
    }

    private Operand calculate(List<Operand> operands) {
        Operand result = new Operand();
        for (Operand operand : operands) {
            result = result.operate(operand, addOperator);
        }
        return result;
    }

}
