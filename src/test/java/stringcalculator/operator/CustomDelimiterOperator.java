package stringcalculator.operator;

import stringcalculator.Number;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static stringcalculator.Number.ZERO;

public class CustomDelimiterOperator implements Operator {

    private static final Pattern PREFIX_PATTERN = Pattern.compile("//(.)\\\\n");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");

    private final List<Number> inputNumbers;

    public CustomDelimiterOperator(String source) {
        this.inputNumbers = toNumbers(removeUnnecessaryPart(source));

    }

    private String removeUnnecessaryPart(String source) {
        return PREFIX_PATTERN.matcher(source)
                .replaceAll("");
    }

    private List<Number> toNumbers(String input) {
        Matcher numberPatternMatcher = NUMBER_PATTERN.matcher(input);
        List<Number> returnNumbers = new ArrayList<>();
        while (numberPatternMatcher.find()) {
            returnNumbers.add(new Number(numberPatternMatcher.group()));
        }
        return returnNumbers;
    }

    @Override
    public int add() {
        return inputNumbers.stream()
                .reduce(ZERO, Number::sum)
                .getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomDelimiterOperator that = (CustomDelimiterOperator) o;
        return Objects.equals(inputNumbers, that.inputNumbers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputNumbers);
    }
}
