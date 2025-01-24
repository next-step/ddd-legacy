package calculator.domain;

import calculator.shared.Constants;

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

    public StringCalculatorDelimiters addDelimiter(final char delimiter) {
        delimiters.add(delimiter);
        return this;
    }

    public String extractAndAddCustomDelimiter(final String input) {
        if (!hasCustomDelimiter(input)) {
            return input;
        }

        String[] tokens = input.split(Constants.LINE_SEPARATOR, 2);

        addDelimiter(tokens[0].charAt(2));
        return tokens[1];
    }

    private boolean hasCustomDelimiter(final String input) {
        return input.startsWith("//") && input.contains(Constants.LINE_SEPARATOR);
    }

    public String getRegex() {
        return delimiters.stream()
                .map(String::valueOf)
                .collect(Collectors.joining("|"));
    }

    public Set<Character> getDelimiters() {
        return delimiters;
    }

    @Override
    public String toString() {
        return delimiters.toString();
    }
}