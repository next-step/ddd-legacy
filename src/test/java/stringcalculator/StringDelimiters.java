package stringcalculator;

import java.util.ArrayList;
import java.util.List;

public class StringDelimiters {
    public static final List<String> DEFAULT_DELIMITERS = List.of(",", ":");

    private final List<String> delimiters = new ArrayList<>(DEFAULT_DELIMITERS);

    public StringDelimiters() {}

    public StringDelimiters(String customDelimiter) {
        this.delimiters.add(customDelimiter);
    }

    public String asRegex() {
        return "[" + String.join("", delimiters) + "]";
    }
}
