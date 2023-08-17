package calculator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Delimiter {
    private final Set<Character> delimiters = new HashSet<>(List.of(':', ','));

    public void addNewDelimiterIfExist(final TargetString targetString) {
        targetString.getDelimiterOrNull().ifPresent(delimiters::add);
    }

    public boolean contains(char ch) {
        return delimiters.contains(ch);
    }
}
