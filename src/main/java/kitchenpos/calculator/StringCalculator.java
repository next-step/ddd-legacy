package kitchenpos.calculator;

import org.springframework.util.StringUtils;

import java.util.Arrays;

public class StringCalculator {
    private static final Integer DEFAULT_VALUE = 0;

    private final StringSplitter stringSplitter;

    public StringCalculator() {
        this.stringSplitter = new StringSplitter();
    }

    public int sum(String text) {
        if (!StringUtils.hasText(text)) {
            return DEFAULT_VALUE;
        }

        String[] tokens = stringSplitter.split(text);
        NaturalNumber sum = Arrays.stream(tokens)
                .map(NaturalNumber::new)
                .reduce(NaturalNumber::add)
                .get();

        return sum.getValue();
    }
}
