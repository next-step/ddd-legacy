package stringcalculator;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringCalculator {
    private static final int DEFAULT_RESULT = 0;
    private static final String DEFAULT_DELIMITER = ",|:";

    private static final Pattern PATTERN = Pattern.compile("//(.)\n(.*)");
    private static final int DELIMITER_POSITION = 1;
    private static final int SOURCE_POSITION = 2;

    public static int calculate(String source) {
        if (source == null || source.isBlank()) {
            return DEFAULT_RESULT;
        }

        return sumPositives(toPositives(split(source)));
    }

    private static String[] split(String source) {
        String numericStrings = source;
        String delimiter = DEFAULT_DELIMITER;

        Matcher m = PATTERN.matcher(source);
        if (m.find()) {
            numericStrings = m.group(SOURCE_POSITION);
            delimiter = m.group(DELIMITER_POSITION);
        }

        return numericStrings.split(delimiter);
    }

    private static List<PositiveInteger> toPositives(String[] strings) {
        try {
            return Arrays.stream(strings)
                    .map(PositiveInteger::new)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("숫자가 아니면 계산할 수 없습니다.");
        }
    }

    private static int sumPositives(List<PositiveInteger> positives) {
        return positives.stream()
                .reduce(PositiveInteger::add)
                .map(PositiveInteger::getNumber)
                .orElseThrow();
    }
}
