package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenizerUtilsImpl implements TokenizerUtils{

    private static final Pattern CUSTOM_SEPERATOR_PATTERN = Pattern.compile("//(.*)\\n(.*)");
    private static final String DEFAULT_SEPERATOR = "[,:]";

    @Override
    public String[] parse(String input) {
        Matcher matcher = CUSTOM_SEPERATOR_PATTERN.matcher(input);
        if (matcher.find()) {
            String customSeperator = matcher.group(1);
            input = matcher.group(2);
            return parse(input, customSeperator);
        }

        return input.split(DEFAULT_SEPERATOR);
    }

    private String[] parse(String input, String customSeperator) {
        return input.split(customSeperator);
    }
}
