package stringcalculator;

import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringCalculator {
    private final Pattern customSeparatorPattern = Pattern.compile("//(.+?)\\n(.*)");
    private final List<String> defaultSeparators = List.of(":", ",");

    public StringCalculator() {
    }

    public Integer calculate(String input) {
        if (Strings.isEmpty(input)) {
            return 0;
        }

        List<String> separators = new ArrayList<>(this.defaultSeparators);
        SeparatorParser separatorParser = new SeparatorParser(this.customSeparatorPattern);
        CustomSeparator customSeparator = separatorParser.parseSeparator(input);

        if (customSeparator.isParsedStatus()) {
            input = customSeparator.getParsedInput();
            separators.add(customSeparator.getSeparator());
        }

        String[] splitInputNumbers = input.split(getJoinedSeparatorsString(separators));
        int calculatedNumber = 0;

        for (String stringNumber : splitInputNumbers) {
            int addedNumber = parseInteger(stringNumber);
            calculatedNumber += addedNumber;
        }


        return calculatedNumber;
    }

    private String getJoinedSeparatorsString(List<String> separators) {
        return separators.stream()
            .map(Pattern::quote)
            .collect(Collectors.joining("|"));
    }

    private int parseInteger(String stringNumber) {
        int number = 0;
        try {
            number = Integer.parseInt(stringNumber);
        } catch (NumberFormatException e) {
            throw new RuntimeException("invalid integer value: " + stringNumber);
        }

        if (number < 0) {
            throw new RuntimeException("negative number is not allowed.");
        }

        return number;
    }
}
