package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenizerUtilsImpl implements TokenizerUtils {

    private static final Pattern CUSTOM_SEPERATOR_PATTERN = Pattern.compile("//(.*)\\n(.*)");
    private static final String DEFAULT_SEPERATOR = "[,:]";
    private static final int SEPERATOR_PATTERN_INDEX = 1;
    private static final int INPUT_PATTERN_INDEX = 2;

    @Override
    public String[] parse(String input) {
        Matcher matcher = CUSTOM_SEPERATOR_PATTERN.matcher(input);
        if (matcher.find()) {
            String customSeperator = matcher.group(SEPERATOR_PATTERN_INDEX);
            input = matcher.group(INPUT_PATTERN_INDEX);
            return parse(input, customSeperator);
        }

        return input.split(DEFAULT_SEPERATOR);
    }

    private String[] parse(String input, String customSeperator) {
        return input.split(customSeperator);
    }
}
