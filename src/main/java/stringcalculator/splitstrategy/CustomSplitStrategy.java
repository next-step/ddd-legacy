package stringcalculator.splitstrategy;

import stringcalculator.SplitStrategy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomSplitStrategy implements SplitStrategy {
    private static final Pattern MATCH_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int CUSTOM_DELIMITER_POSITION = 1;
    private static final int TARGET_TEXT_POSITION = 2;

    @Override
    public String[] split(String text) {
        Matcher m = MATCH_PATTERN.matcher(text);

        String customDelimiter = "";
        if (m.find()) {
            customDelimiter = m.group(CUSTOM_DELIMITER_POSITION);
        }

        return m.group(TARGET_TEXT_POSITION).split(customDelimiter);
    }

    @Override
    public boolean canSplit(String text) {
        return MATCH_PATTERN.matcher(text).matches();
    }

}
