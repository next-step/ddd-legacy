package calculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class StringOperands {
    private static final int EMPTY_SIZE = 0;

    private final List<StringOperand> operands = new ArrayList<>();

    private StringOperands(final String operandsText, final String delimiterRegex) {
        if (!operandsText.isEmpty()) {
            Arrays.stream(operandsText.split(delimiterRegex))
                    .map(StringOperand::of)
                    .forEach(operands::add);
        }
    }

    public static StringOperands of(final String operandsText, final String delimiterRegex) {
        return new StringOperands(operandsText, delimiterRegex);
    }

    public boolean isEmpty() {
        return operands.size() == EMPTY_SIZE;
    }

    public Stream<StringOperand> stream() {
        return operands.stream();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (Objects.isNull(o) || getClass() != o.getClass()) {
            return false;
        }

        StringOperands that = (StringOperands) o;
        return operands.equals(that.operands);
    }

    @Override
    public int hashCode() {
        return operands.hashCode();
    }
}
