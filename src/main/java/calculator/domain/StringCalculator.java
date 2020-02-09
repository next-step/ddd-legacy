package calculator.domain;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringCalculator {

    private static final String CUSTOM_DELIMITER_PATTERN = "//(.)\n(.*)";
    private String delimiter = ",|:";


    public StringCalculator() {
    }

    public int add(String inputString) {
        if(StringUtils.isEmpty(inputString)) return 0;

        String[] tokens = getTokens(inputString);

        return Arrays.stream(tokens)
                .peek(token -> {
                    if (isNotNumber(token) || isNegativeNumber(token)) throw new RuntimeException();
                })
                .collect(Collectors.summingInt(Integer::parseInt));
    }

    private String[] getTokens(String inputString) {
        Matcher m = Pattern.compile(CUSTOM_DELIMITER_PATTERN).matcher(inputString);
        if (m.find()) {
            addDelimeter(m.group(1));
            return m.group(2).split(delimiter);
        }
        return inputString.split(delimiter);
    }

    private void addDelimeter(String substring) {
        this.delimiter += "|" + substring;
    }

    private boolean isNegativeNumber(String number) {
        return Integer.parseInt(number) < 0 ? true : false;
    }

    private boolean isNotNumber(String number) {
        try {
            Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return true;
        }
        return false;
    }
}
