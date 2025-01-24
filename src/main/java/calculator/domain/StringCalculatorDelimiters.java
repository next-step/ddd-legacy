package calculator.domain;

import calculator.base.Constants;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StringCalculatorDelimiters {
    private static final List<Character> DEFAULT_DELIMITERS = List.of(',', ':');
    private static final int SPLIT_LIMIT = 2; // 최대 2개로 split
    private static final int CUSTOM_DELIMITER_INDEX = 2; // "//;"에서 구분자 위치

    private final Set<Character> delimiters = new HashSet<>();

    private StringCalculatorDelimiters() {
        delimiters.addAll(DEFAULT_DELIMITERS);
    }

    public static StringCalculatorDelimiters create() {
        return new StringCalculatorDelimiters();
    }

    public void addDelimiter(final char delimiter) {
        delimiters.add(delimiter);
    }

    public String extractAndAddCustomDelimiter(final String input) {
        if (!hasCustomDelimiter(input)) {
            return input;
        }

        String[] tokens = input.split(Constants.LINE_SEPARATOR, SPLIT_LIMIT);

        final String customDelimiterDefinition = tokens[0]; // "//;"
        final String numericInput = tokens[1]; // "1;2;3"

        addDelimiter(customDelimiterDefinition.charAt(CUSTOM_DELIMITER_INDEX));

        return numericInput;
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