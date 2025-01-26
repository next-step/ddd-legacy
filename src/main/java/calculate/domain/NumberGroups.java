package calculate.domain;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberGroups {
    private static final String DEFAULT_DELIMITER = "[,:]";
    private static final String CUSTOM_DELIMITER = "//(.)\\n(.*)";
    private static final String CUSTOM_DELIMITER_PREFIX = "//";

    private final List<Number> numbers;

    public NumberGroups() {
        this(new ArrayList<>());
    }

    public NumberGroups(final String sentence) {
        this.numbers = formulas(sentence);
    }

    public NumberGroups(final int...numbers) {
        this.numbers = Arrays.stream(numbers).mapToObj(Number::new).toList();
    }

    public NumberGroups(final List<Number> numbers) {
        this.numbers = numbers;
    }

    private boolean hasCustomDelimiter(final String sentence) {
        return sentence != null && sentence.startsWith(CUSTOM_DELIMITER_PREFIX);
    }

    private List<Number> formulas(final String sentence) {
        if(sentence == null || sentence.isEmpty()) {
            return List.of(new Number(0));
        }
        if (hasCustomDelimiter(sentence)) {
            return customDelimiterSplit(sentence);
        }

        return Arrays.stream(sentence.split(DEFAULT_DELIMITER))
                .map(Number::new)
                .toList();
    }

    private List<Number> customDelimiterSplit(final String sentence) {
        final Matcher m = Pattern.compile(CUSTOM_DELIMITER).matcher(sentence);
        if(m.find()) {
            final String delimiter = m.group(1);
            final String formula = m.group(2);
            return Arrays.stream(formula.split(delimiter))
                    .map(Number::new)
                    .toList();
        }
        return Collections.emptyList();
    }

    public int sum() {
        return numbers.stream()
                .mapToInt(Number::getValue)
                .sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumberGroups numberGroups1 = (NumberGroups) o;
        return Objects.equals(numbers, numberGroups1.numbers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numbers);
    }

    @Override
    public String toString() {
        return "Numbers{" +
                "numbers=" + numbers +
                '}';
    }

}
