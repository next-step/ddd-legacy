package calculator;

import java.util.regex.Pattern;

public class SeparatorCalculateStrategy extends AbstractCalculateStrategy {

    private static final String SEPARATOR = "[,:]";
    private static final Pattern COMPILED_SEPARATOR_PATTERN = Pattern.compile("[,:]");

    @Override
    public boolean isTarget(final String text) {
        return COMPILED_SEPARATOR_PATTERN.matcher(text).find();
    }

    @Override
    public int calculate(final String text) {
        return calculateWithDelimiter(text, SEPARATOR);
    }

}

