package stringcalculator.factory.splitter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomSplitter implements Splitter {

    public static final String CUSTOM_DELIMITER_REGEX = "//(.)\n(.*)";
    private static final int CUSTOM_DELIMITER_INDEX = 1;
    private static final int NUMBER_VALUE_INDEX = 2;

    @Override
    public List<String> split(String value) {
        Matcher m = Pattern.compile(CUSTOM_DELIMITER_REGEX).matcher(value);
        m.find();
        String customDelimiter = m.group(CUSTOM_DELIMITER_INDEX);
        String[] split = m.group(NUMBER_VALUE_INDEX).split(customDelimiter);
        return List.of(split);
    }
}
