package stringcalculator.splitstrategy;

import org.springframework.boot.convert.Delimiter;
import stringcalculator.SplitStrategy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomSplitStrategy implements SplitStrategy {
    private static final Pattern MATCH_PATTERN = Pattern.compile("//(.)\n(.*)");

    @Override
    public String[] split(String text) {
        Matcher m = MATCH_PATTERN.matcher(text);

        String customDelimiter = "";
        if (m.find()) {
            customDelimiter = m.group(1);
        }

        return m.group(2).split(customDelimiter);
    }

    @Override
    public boolean canSplit(String text) {
        return MATCH_PATTERN.matcher(text).matches();
    }

}
