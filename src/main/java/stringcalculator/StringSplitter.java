package stringcalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class StringSplitter {
    private static final String DELIMTER = "|";
    private static final String MATCH_SPECIAL = "\\?|\\.|\\$|\\|";
    private static final String ADD_SPECIAL_DELIMITER = "\\";
    private List<String> delimiters;

    public StringSplitter(List<String> delimiters) {
        this.delimiters = delimiters;
    }

    public StringSplitter addDelimiter(String delimiter) {
        List<String> newDelimiters = new ArrayList<>(delimiters);
        if (delimiter.matches(MATCH_SPECIAL)) {
            delimiter = ADD_SPECIAL_DELIMITER + delimiter;
        }
        newDelimiters.add(delimiter);
        return new StringSplitter(newDelimiters);
    }

    public List<String> split(String text) {
        String delimiter = String.join(DELIMTER, delimiters);
        return Arrays.stream(text.split(delimiter)).collect(toList());
    }
}
