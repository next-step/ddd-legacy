package stringcalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

public class StringSplitter {
    private static final List<String> DEFAULT_DELIMITER = Arrays.asList(",", ":");
    private static final String DELIMTER = "|";
    private static final String MATCH_SPECIAL = "\\?|\\.|\\$|\\|";
    private static final String ADD_SPECIAL_DELIMITER = "\\";
    private static final Pattern CUSTOM_PATTERN = Pattern.compile("//(.)\n(.*)");

    private List<String> delimiters;

    public StringSplitter() {
        this(new ArrayList<>(DEFAULT_DELIMITER));
    }

    public StringSplitter(List<String> delimiters) {
        this.delimiters = delimiters;
    }

    public List<String> split(String text) {
        String checkedText = matchCustom(text);
        String delimiter = String.join(DELIMTER, delimiters);
        return Arrays.stream(checkedText.split(delimiter)).collect(toList());
    }

    private String matchSpecial(String delimiter) {
        if (delimiter.matches(MATCH_SPECIAL)) {
            delimiter = ADD_SPECIAL_DELIMITER + delimiter;
        }
        return delimiter;
    }

    private String matchCustom(String text) {
        Matcher m = CUSTOM_PATTERN.matcher(text);
        if(m.find()) {
            delimiters.add(matchSpecial(m.group(1)));
            return m.group(2);
        }
        return text;
    }
}
