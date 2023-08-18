package stringcalculator.splitstrategy;

import stringcalculator.SplitStrategy;

import java.util.regex.Pattern;

public class CommaAndColonSplitStrategy implements SplitStrategy {
    private static final Pattern MATCH_PATTERN = Pattern.compile("\\d+(?:[,:]\\d+)*");
    private static final String DELIMITER = ",|:";

    @Override
    public String[] split(String text) {
        return text.split(DELIMITER);
    }

    @Override
    public boolean canSplit(String text) {
        return MATCH_PATTERN.matcher(text).matches();
    }
}
