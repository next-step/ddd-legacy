package stringcalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

public class StringSplitter {
    private static final String DELIMTER = "|";
    private static final String MATCH_SPECIAL = "\\?|\\.|\\$|\\|";
    private static final String ADD_SPECIAL_DELIMITER = "\\";
    private static final Pattern CUSTOM_PATTERN = Pattern.compile("//(.)\n(.*)");

    private List<String> delimiters;

    public StringSplitter(List<String> delimiters) {
        this.delimiters = delimiters;
    }

    public StringSplitter matchCustom(String text) {
        Matcher m = CUSTOM_PATTERN.matcher(text);
        if(m.find()) {
            return addDelimiter(m.group(1));
        }
        return this;
    }

    public List<String> split(String text) {
        String delimiter = String.join(DELIMTER, delimiters);
        return Arrays.stream(matchText(text).split(delimiter)).collect(toList());
    }

    private String matchSpecial(String delimiter) {
        if (delimiter.matches(MATCH_SPECIAL)) {
            delimiter = ADD_SPECIAL_DELIMITER + delimiter;
        }
        return delimiter;
    }

    private String matchText(String text) {
        Matcher m = CUSTOM_PATTERN.matcher(text);
        if(m.find()) {
            return m.group(2);
        }
        return text;
    }

    private StringSplitter addDelimiter(String delimiter) {
        List<String> newDelimiters = new ArrayList<>(delimiters);
        newDelimiters.add(matchSpecial(delimiter));
        return new StringSplitter(newDelimiters);
    }


}
