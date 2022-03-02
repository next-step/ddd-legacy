package stringcalculator;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    public StringCalculator() {
    }

    private Boolean checkIfNull(String text) {
        if (StringUtils.hasText(text)) {
            return false;
        }
        return true;
    }

    private Matcher findCustomDelimiter(String text) {
        return Pattern.compile("//(.)\n(.*)").matcher(text);
    }

    private String[] splitString(String text) {
        Matcher m = findCustomDelimiter(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            return m.group(2).split(customDelimiter);
        }
        return text.split(",|:");
    }

    private int addTokens(String[] tokens) {
        return Arrays.stream(tokens)
                .map(number -> Integer.parseInt(number))
                .peek(number -> {
                    if (number < 0) throw new RuntimeException();
                })
                .reduce(0, Integer::sum);
    }

    public int add(String text) {
        if (checkIfNull(text)) {
            return 0;
        }
        return addTokens(splitString(text));
    }
}
