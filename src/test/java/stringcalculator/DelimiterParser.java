package stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DelimiterParser {

    private final Pattern regexPattern = Pattern.compile(ValidationRegex.CUSTOM_DELIMITER_REGEX.getRegex());

    public DelimiterParser() {
    }

    public String[] parseDelimiter(String text) {

        String[] parsedNumbers = text.split(",|:");

        Matcher matcher = regexPattern.matcher(text);

        if(matcher.find()) {
            String delimiter = matcher.group(1);
            parsedNumbers = matcher.group(2).split(delimiter);
        }

        return parsedNumbers;

    }

}
