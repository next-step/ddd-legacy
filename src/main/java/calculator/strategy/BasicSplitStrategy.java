package calculator.strategy;

import calculator.Numbers;

import java.util.regex.Pattern;

public class BasicSplitStrategy implements NumbersSplitStrategy {

    private static final String DELIMITER_REGEX = "[,:]";
    private static final Pattern MATCH_PATTERN = Pattern.compile("-?\\d+(?:[,:]-?\\d+)*");

    @Override
    public Numbers extract(String input) {
        return new Numbers(input.split(DELIMITER_REGEX));
    }

    @Override
    public boolean isMatchPattern(String input) {
        return MATCH_PATTERN.matcher(input).matches();
    }
}
