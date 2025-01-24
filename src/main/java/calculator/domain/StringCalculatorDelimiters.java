package calculator.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StringCalculatorDelimiters {
    private static final List<Character> DEFAULT_DELIMITERS = List.of(',', ':');

    private final Set<Character> delimiters = new HashSet<>();

    private StringCalculatorDelimiters() {
        delimiters.addAll(DEFAULT_DELIMITERS);
    }

    public static StringCalculatorDelimiters create() {
        return new StringCalculatorDelimiters();
    }

    public StringCalculatorDelimiters addDelimiter(char delimiter) {
        delimiters.add(delimiter);
        return this;
    }

    public String extractAndAddCustomDelimiter(String input) {
        if (!hasCustomDelimiter(input)) {
            return input;
        }

        String[] tokens = input.split("\n", 2);

        addDelimiter(tokens[0].charAt(2));
        return tokens[1];
    }

    private boolean hasCustomDelimiter(String input) {
        return input.startsWith("//") && input.contains("\n");
    }

    public String getRegex() {
        return delimiters.stream()
                .map(String::valueOf)
                .collect(Collectors.joining("|"));
    }

    public Set<Character> getDelimiters() {
        return delimiters;
    }
}