package stringcalculator.operator;

import stringcalculator.delimiter.DefaultDelimiter;
import stringcalculator.number.Number;
import stringcalculator.number.ZeroNumber;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultOperator implements Operator {

    private final List<Number> numbers;

    private DefaultOperator(List<Number> numbers) {
        this.numbers = numbers;
    }

    public static DefaultOperator from(String input) {
        return new DefaultOperator(
                Arrays.stream(new DefaultDelimiter(input).split())
                        .map(Number::new)
                        .collect(Collectors.toList())

        );
    }

    @Override
    public int add() {
        return numbers.stream()
                .reduce(new ZeroNumber(), Number::sum)
                .getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DefaultOperator that = (DefaultOperator) o;

        return numbers != null ? numbers.equals(that.numbers) : that.numbers == null;
    }

    @Override
    public int hashCode() {
        return numbers != null ? numbers.hashCode() : 0;
    }
}
