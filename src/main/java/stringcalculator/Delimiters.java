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

    public String[] split(final String text) {
        String modifiedText = text;
        for (String delimiter : delimiters) {
            modifiedText = modifiedText.replace(delimiter, " ");
        }
        return modifiedText.split(" ");
    }

}
