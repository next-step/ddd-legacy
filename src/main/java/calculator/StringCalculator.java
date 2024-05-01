package calculator;

import calculator.number.*;
import utils.*;

import java.util.regex.*;

import static calculator.number.Number.ZERO;

public class StringCalculator {

    private static final String DEFAULT_DELIMITER_REGEX = "[,:]";

    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\\n(.*)");
    private static final int CUSTOM_DELIMITER_INDEX = 1;
    private static final int TARGET_TEXT_INDEX = 2;


    public int add(String text) {
        if (StringUtils.isBlank(text)) {
            return ZERO.getValue();
        }

        var texts = separate(text);
        var positives = new Positives(texts);

        return positives.sum();
    }

    private String[] separate(final String text) {
        var matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (matcher.find()) {
            var customDelimiter = matcher.group(CUSTOM_DELIMITER_INDEX);
            return matcher.group(TARGET_TEXT_INDEX).split(customDelimiter);
        }

        return text.split(DEFAULT_DELIMITER_REGEX);
    }

}
