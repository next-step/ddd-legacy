package stringcalculator;

import java.util.HashSet;
import java.util.Set;

public class Delimiters {
    private final Set<String> delimiters;

    public Delimiters() {
        delimiters = new HashSet<>();
        delimiters.add(",");
        delimiters.add(":");
    }

    public void addCustomDelimiter(String delimiter) {
        delimiters.add(delimiter);
    }

    public String[] split(String text) {
        for (String delimiter : delimiters) {
            text = text.replace(delimiter, " ");
        }
        return text.split(" ");
    }
}
