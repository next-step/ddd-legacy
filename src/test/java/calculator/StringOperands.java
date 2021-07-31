package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class StringOperands {
    private static final int EMPTY_STANDARD_SIZE = 1;
    private static final int FIRST_INDEX = 0;

    private final List<String> operands;

    private StringOperands(String operandsText, String delimiterRegex) {
        this.operands = Arrays.asList(operandsText.split(delimiterRegex));
    }

    public static StringOperands of(String operandsText, String delimiterRegex) {
        return new StringOperands(operandsText, delimiterRegex);
    }

    public boolean isEmpty() {
        int size = operands.size();
        return size < EMPTY_STANDARD_SIZE || size == EMPTY_STANDARD_SIZE && operands.get(FIRST_INDEX).isEmpty();
    }

    public Stream<String> stream() {
        return operands.stream();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (Objects.isNull(o) || getClass() != o.getClass()) {
            return false;
        }

        StringOperands that = (StringOperands) o;
        return Objects.equals(operands, that.operands);
    }

    @Override
    public int hashCode() {
        return Objects.isNull(operands) ? 0 : operands.hashCode();
    }
}
