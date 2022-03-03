package stringcalculator.operator;

import stringcalculator.number.Number;
import stringcalculator.operator.pattern.NumberPattern;
import stringcalculator.number.ZeroNumber;
import stringcalculator.operator.pattern.PrefixPattern;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class CustomOperator implements Operator {

    private final List<Number> numbers;

    private CustomOperator(List<Number> numbers) {
        this.numbers = numbers;
    }

    public static CustomOperator from(String input) {
        return new CustomOperator(makeNumbers(new PrefixPattern(input).removeUnnecessary()));
    }

    private static List<Number> makeNumbers(String input) {
        Matcher numberPatternMatcher = new NumberPattern(input).getMatcher();
        List<Number> numbers = new ArrayList<>();
        while (numberPatternMatcher.find()) {
            numbers.add(new Number(numberPatternMatcher.group()));
        }
        return numbers;
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

        CustomOperator that = (CustomOperator) o;

        return numbers != null ? numbers.equals(that.numbers) : that.numbers == null;
    }

    @Override
    public int hashCode() {
        return numbers != null ? numbers.hashCode() : 0;
    }

}
