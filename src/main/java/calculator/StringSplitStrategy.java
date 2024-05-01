package calculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringSplitStrategy implements SplitStrategy {
    private static final Pattern PATTERN = Pattern.compile("//(.)\n(.*)");

    @Override
    public List<Integer> split(String input) {
        if (isNullAndEmpty(input)) {
            return List.of(0);
        }
        if (isInputOnlyOne(input)) {
            return List.of(Integer.valueOf(input));
        }
        return setSplitResult(input);
    }

    private boolean isNullAndEmpty(String input) {
        return input == null || input.isEmpty();
    }

    private boolean isInputOnlyOne(String input) {
        return input.length() == 1;
    }

    private List<Integer> setSplitResult(String input) {
        String[] splitResult = null;
        Matcher matcher = PATTERN.matcher(input);
        if (isContainComma(input) || isContainColon(input)){
            splitResult = input.split(",|:");
        }
        if (matcher.find()) {
            String customDelimiter = matcher.group(1);
            splitResult = matcher.group(2).split(customDelimiter);
        }
        return Arrays.stream(splitResult)
                .map(Integer::valueOf)
                .collect(Collectors.toList());
    }

    private boolean isContainComma(String input) {
        return input.contains(",");
    }

    private boolean isContainColon(String input) {
        return input.contains(";");
    }
}