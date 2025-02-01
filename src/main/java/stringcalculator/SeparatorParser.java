package stringcalculator;

import jakarta.annotation.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeparatorParser {

    private final Pattern separatorPattern;

    public SeparatorParser(Pattern separatorPattern) {
        this.separatorPattern = separatorPattern;
    }

    public CustomSeparator parseSeparator(String input) {
        Matcher matcher = separatorPattern.matcher(input);
        if (matcher.find()) {
            return new CustomSeparator(matcher.group(2), matcher.group(1));
        }

        return new CustomSeparator();
    }

    public boolean hasCustomSeparator(String input) {
        Matcher matcher = separatorPattern.matcher(input);
        return matcher.find();
    }
}
