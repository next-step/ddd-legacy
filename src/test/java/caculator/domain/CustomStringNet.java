package caculator.domain;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomStringNet implements StringNet {

    public static final Pattern CUSTOM_DELIMITER = Pattern.compile("//(.)\n(.*)");
    public static final int CUSTOM_DELIMITER_INDEX = 1;
    public static final int CUSTOM_NUMBERS_INDEX = 2;

    @Override
    public String[] strain(String includedDelimiter) {
        Matcher matcher = CUSTOM_DELIMITER.matcher(includedDelimiter);
        matcher.find();

        String customDelimiter = matcher.group(CUSTOM_DELIMITER_INDEX);
        String excludedDelimiter = matcher.group(CUSTOM_NUMBERS_INDEX);
        return excludedDelimiter.split(customDelimiter);
    }


}
