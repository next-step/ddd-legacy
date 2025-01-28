package calculator;

import exception.InvalidNumberFormatException;
import exception.NotPositiveNumberException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    public int add(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }

        String[] numberString = splitNumbers(text);
        return Arrays.stream(numberString).mapToInt(this::parseAndValidatePositive).sum();
    }

    public String[] splitNumbers(String text) {
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        String delimiters = ",|:";

        if (m.find()) {
            String customDelimiter = m.group(1);
            delimiters = delimiters + "|" + customDelimiter;
            text = m.group(2);
        }

        return text.split(delimiters);
    }

    public int parseAndValidatePositive(String element) {
        if (!element.matches("-?\\d+")) {
            throw new InvalidNumberFormatException();
        }

        int intValue = Integer.parseInt(element);
        if (intValue < 0) {
            throw new NotPositiveNumberException();
        }

        return intValue;
    }
}
