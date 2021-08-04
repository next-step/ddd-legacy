package stringcalculator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringSplitter {

    public static List<String> split(final String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }

        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            String[] tokens = m.group(2).split(customDelimiter);
            return Arrays.asList(tokens);
        }

        return Arrays.asList(text.split("[,:]"));
    }
}
