package calculator;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private static final String DEFAULT_SPLIT_REGEX = "[,:]";
    private static final String CUSTOM_SPLIT_REGEX = "//(.)\\n(.*)";
    private static final String NUMBER_REGEX = "^[0-9]+$";

    public static int add(String inputText) {
        if (StringUtils.isEmpty(inputText)) {
            return 0;
        }

        return Arrays.stream(parseText(inputText))
                .filter(StringCalculator::validate)
                .mapToInt(Integer::parseInt)
                .sum();
    }

    private static String[] parseText(String inputText) {
        Matcher matcher = Pattern.compile(CUSTOM_SPLIT_REGEX).matcher(inputText);
        if (matcher.find()) {
            String customDelimiter = matcher.group(1);
            return matcher.group(2).split(customDelimiter);
        }
        return inputText.split(DEFAULT_SPLIT_REGEX);
    }

    private static boolean validate(String number) {
        if (!number.matches(NUMBER_REGEX) || Integer.parseInt(number) < 0) {
            throw new RuntimeException();
        }
        return true;
    }
}