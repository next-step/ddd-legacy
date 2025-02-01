package stringcalculator;

import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.Arrays;
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

        if (separatorParser.hasCustomSeparator(input)) {
            CustomSeparator customSeparator = separatorParser.parseSeparator(input);
            input = customSeparator.getParsedInput();
            separators.add(customSeparator.getSeparator());
        }

        String[] splitInputNumbers = input.split(getJoinedSeparatorsString(separators));
        PositiveStringNumbers positiveStringNumbers = new PositiveStringNumbers(splitInputNumbers);

        return positiveStringNumbers.addAllNumber();
    }

    private String getJoinedSeparatorsString(List<String> separators) {
        return separators.stream()
            .map(Pattern::quote)
            .collect(Collectors.joining("|"));
    }
}
