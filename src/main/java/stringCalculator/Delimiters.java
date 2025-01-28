package stringCalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Delimiters {
    private static final List<String> DEFAULT_DELIMITERS = Arrays.asList(",", ":");
    private final List<String> delimiters;

    private Delimiters() {
        this.delimiters = new ArrayList<>(DEFAULT_DELIMITERS);
    }

    private Delimiters(final String custom) {
        this.delimiters = new ArrayList<>(DEFAULT_DELIMITERS);
        this.delimiters.add(custom);
    }

    public static Delimiters ofDefaults() {
        return new Delimiters();
    }

    public static Delimiters customize(final String custom) {
        return new Delimiters(custom);
    }

    public List<String> list() {
        return delimiters;
    }
}
