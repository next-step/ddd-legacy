package calculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringSplitStrategy implements SplitStrategy {
    private final List<Integer> splitResult;

    public StringSplitStrategy() {
        splitResult = new ArrayList<>();
    }

    @Override
    public List<Integer> split(String input) {
        if (isNullAndEmpty(input)) {
            splitResult.add(0);
        } else if (isInputOnlyOne(input)) {
            splitResult.add(Integer.valueOf(input));
        } else if (isContainComma(input) || isContainColon(input)) {
            setSplitResult(input);
        }

        return splitResult;
    }

    private boolean isNullAndEmpty(String input) {
        return input == null || input.isEmpty();
    }

    private boolean isInputOnlyOne(String input) {
        return input.length() == 1;
    }

    private boolean isContainComma(String input) {
        return input.contains(",");
    }

    private boolean isContainColon(String input) {
        return input.contains(";");
    }

    private void setSplitResult(String input) {
        String[] splitResult = customSplit(input);
        for (String value : splitResult) {
            this.splitResult.add(Integer.valueOf(value));
        }
    }

    private String[] customSplit(String input) {
        return input.split(",|:");
    }
}