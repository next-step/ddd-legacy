package kitchenpos.calculator;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final Pattern CUSTOM_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final String DEFAULT_DELIMITERS = "[,:]";
    private static final Integer DEFAULT_VALUE = 0;

    private final ValidationStrategy validationStrategy;

    public StringCalculator(ValidationStrategy validationStrategy) {
        this.validationStrategy = validationStrategy;
    }

    public int sum(String text) {
        if (!StringUtils.hasText(text)) {
            return DEFAULT_VALUE;
        }

        String[] strings = split(text);
        int[] ints = toInts(strings);
        validate(ints);
        return sum(ints);
    }

    private String[] split(String text) {
        Matcher m = CUSTOM_PATTERN.matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            return m.group(2).split(customDelimiter);
        }
        return text.split(DEFAULT_DELIMITERS);
    }

    private int[] toInts(String[] strings) {
        int[] ints = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            ints[i] = Integer.parseInt(strings[i]);
        }
        return ints;
    }

    private int sum(int[] ints) {
        int sum = 0;
        for (int i : ints) {
            sum += i;
        }
        return sum;
    }

    private void validate(int[] ints) {
        for (int i : ints) {
            throwOnInvalid(i);
        }
    }

    private void throwOnInvalid(int num) {
        if (!validationStrategy.isValid(num)) {
            throw new RuntimeException();
        }
    }
}
