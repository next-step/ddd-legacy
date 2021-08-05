package stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringSplitter {

    public static SplitTexts split(final String text) {
        if (text == null || text.isEmpty()) {
            return new SplitTexts();
        }

        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            String[] tokens = m.group(2).split(customDelimiter);
            return new SplitTexts(tokens);
        }

        return new SplitTexts(text.split("[,:]"));
    }
}
