package stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringParser {
    private static final Pattern pattern = Pattern.compile("//(.)\n(.*)");

    private Matcher findCustomDelimiter(String text) {
        return pattern.matcher(text);
    }

    public String[] splitStringToToken(String text){
        Matcher matcher = findCustomDelimiter(text);
        if (matcher.find()) {
            String customDelimiter = matcher.group(1);
            return matcher.group(2).split(customDelimiter);
        }
        return text.split(",|:");
    }
}
