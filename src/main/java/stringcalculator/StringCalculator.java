package stringcalculator;

import jakarta.annotation.Nullable;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringCalculator {
    private static final String CUSTOM_SEPARATOR_REGEXP = "//(.+?)\\n(.*)";

    private final List<String> defaultSeparators = List.of(":", ",");

    private String customSeparatorRemovedInput;

    public StringCalculator() {
    }

    public Integer calculate(String input) {
        String forCalculationInput = input;
        if (Strings.isEmpty(forCalculationInput)) {
            return 0;
        }

        List<String> separators = new ArrayList<>(this.defaultSeparators);
        String customSeparator = getCustomSeparator(forCalculationInput);
        if (customSeparator != null) {
            separators.add(customSeparator);
            forCalculationInput = getCustomSeparatorRemovedInput(input);
        }

        String[] splitInputNumbers = forCalculationInput.split(getJoinedSeparatorsString(separators));
        int calculatedNumber = 0;

        for (String stringNumber : splitInputNumbers) {
            int addedNumber = Integer.parseInt(stringNumber);

            checkNegativeInputNumber(addedNumber);

            calculatedNumber += addedNumber;
        }

        return calculatedNumber;
    }

    private String getJoinedSeparatorsString(List<String> separators) {
        return separators.stream()
            .map(Pattern::quote)
            .collect(Collectors.joining("|"));
    }

    private void checkNegativeInputNumber(Integer number) {
        if (number < 0) {
            throw new RuntimeException("negative number is not allowed.");
        }
    }

    @Nullable
    private String getCustomSeparator(String input) {
        Matcher customSeparatorMatcher = Pattern.compile(CUSTOM_SEPARATOR_REGEXP).matcher(input);
        if (customSeparatorMatcher.find()) {
            this.customSeparatorRemovedInput = customSeparatorMatcher.group(2);
            return customSeparatorMatcher.group(1);
        }
        return null;
    }

    @Nullable
    private String getCustomSeparatorRemovedInput(String input) {
        if (customSeparatorRemovedInput != null) {
            return customSeparatorRemovedInput;
        }

        Matcher customSeparatorMatcher = Pattern.compile(CUSTOM_SEPARATOR_REGEXP).matcher(input);
        if (customSeparatorMatcher.find()) {
            return customSeparatorMatcher.group(2);
        }
        return input;
    }
}
