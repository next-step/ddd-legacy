package calculator;

import java.util.Arrays;
import java.util.List;

public class StringCalculator {
    private static final List<String> DEFAULT_SEPARATORS = Arrays.asList(",", ":");
    private static final String SEPARATOR_DELIMITER = "|";
    private static final int DEFAULT_RESULT = 0;
    
    private List<String> separates;

    public StringCalculator() {
        separates = DEFAULT_SEPARATORS;
    }

    public int add(String text) {
        if (isEmptyText(text)) {
            return DEFAULT_RESULT;
        }

        return Arrays.stream(text.split(getSeparatorRegx()))
                .mapToInt(Integer::valueOf)
                .sum();
    }

    private boolean isEmptyText(String text) {
        return text == null || text.isEmpty();
    }

    private String getSeparatorRegx() {
        return String.join(SEPARATOR_DELIMITER, this.separates);
    }
}
