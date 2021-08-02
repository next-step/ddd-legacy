package calculator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Delimiters {
    private final ArrayList<Character> delimiters;

    public Delimiters(List<Character> delimiters) {
        this.delimiters = new ArrayList<>(delimiters);
    }

    public void add(Character delimiter) {
        delimiters.add(delimiter);
    }

    public String toRegex() {
        var joined = delimiters.stream()
            .map(String::valueOf)
            .collect(Collectors.joining());
        return "[" + joined + "]";
    }
}
