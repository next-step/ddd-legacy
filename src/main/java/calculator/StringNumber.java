package calculator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringNumber {
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("[+]?\\d+");

    public static int sum(List<String> strings) {
        return parseInt(strings).stream()
                .reduce(Integer::sum)
                .orElse(0);
    }

    private static List<Integer> parseInt(List<String> strings) {
        return strings.stream()
                .map(StringNumber::parsePositiveInt)
                .collect(Collectors.toList());
    }

    private static Integer parsePositiveInt(String string) {
        Matcher isNumeric = NUMERIC_PATTERN.matcher(string);
        if (isNumeric.matches()) {
            return Integer.parseInt(string);
        }
        throw new IllegalArgumentException();
    }
}
