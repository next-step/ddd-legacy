package calculator;

import java.util.Set;

public class Delimiter {
    private static final Set<Character> BASIC_DELIMITERS = Set.of(':', ',');
    private volatile char newDelimiter = '\0';

    public Delimiter(final TargetString targetString) {
        targetString.getDelimiterOrNull().ifPresent(delimiter -> newDelimiter = delimiter);
    }

    public boolean contains(char ch) {
        return BASIC_DELIMITERS.contains(ch) || ch == newDelimiter;
    }
}
