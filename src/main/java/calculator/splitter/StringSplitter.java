package calculator.splitter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringSplitter implements Splitter<String> {

    private static final String DEFAULT_DELIMITER = ",|:";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int DELIMITER_GROUP = 1;
    private static final int TARGET_TEXT_GROUP = 2;

    private final Delimiter delimiter;

    public StringSplitter() {
        this.delimiter = new Delimiter(DEFAULT_DELIMITER);
    }

    public String[] split(String target) {
        SplitTargetText targetText = new SplitTargetText(target);
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(target);
        if (matcher.find()) {
            delimiter.addDelimiter(matcher.group(DELIMITER_GROUP));
            targetText = new SplitTargetText(matcher.group(TARGET_TEXT_GROUP));
            return delimiter.split(targetText);
        }

        return delimiter.split(targetText);
    }
}
