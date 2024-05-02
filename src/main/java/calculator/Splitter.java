package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Splitter {

    private static final String DEFAULT_DELIMITER = ",|:";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int DELIMITER_GROUP = 1;
    private static final int TARGET_TEXT_GROUP = 2;

    private final Delimiter delimiter;

    public Splitter() {
        this.delimiter = new Delimiter(DEFAULT_DELIMITER);
    }

    public String[] split(String text) {
        SplitTargetText targetText = new SplitTargetText(text);
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (matcher.find()) {
            delimiter.addDelimiter(matcher.group(DELIMITER_GROUP));
            targetText = new SplitTargetText(matcher.group(TARGET_TEXT_GROUP));
            return delimiter.split(targetText);
        }

        return delimiter.split(targetText);
    }
}
