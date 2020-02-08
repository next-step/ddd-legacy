package calculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringParser {
    private static final String DEFAULT_SPLIT_REGEX = "[,:]";
    private static final Pattern CUSTOM_SPLIT_REGEX = Pattern.compile("//(.)\\n(.*)");

    public String[] parse(String inputText) {
        Matcher matcher = CUSTOM_SPLIT_REGEX.matcher(inputText);
        if (matcher.find()) {
            String customDelimiter = matcher.group(1);
            return matcher.group(2).split(customDelimiter);
        }
        return inputText.split(DEFAULT_SPLIT_REGEX);
    }
}
