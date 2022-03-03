package stringcalculator.operator;

import stringcalculator.number.Number;
import stringcalculator.number.ZeroNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomOperator implements Operator {

    private final List<Number> numbers;

    private CustomOperator(List<Number> numbers) {
        this.numbers = numbers;
    }

    public static CustomOperator from(String input) {
        return new CustomOperator(makeNumbers(new PrefixPattern(input).value()));
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

    private static class PrefixPattern {

        private final String origin;
        private final Pattern pattern;

        public PrefixPattern(String origin, Pattern pattern) {
            this.origin = origin;
            this.pattern = pattern;
        }

        public PrefixPattern(String origin) {
            this(origin, Pattern.compile("//(.)\\\\n"));
        }

        public String value() {
            return pattern.matcher(origin).replaceAll("");
        }

    }

    private static class NumberPattern {

        private final String origin;
        private final Pattern pattern;

        public NumberPattern(String origin, Pattern pattern) {
            this.origin = origin;
            this.pattern = pattern;
        }

        public NumberPattern(String origin) {
            this(origin, Pattern.compile("\\d+"));
        }

        public Matcher getMatcher() {
            return pattern.matcher(origin);
        }

    }

}
