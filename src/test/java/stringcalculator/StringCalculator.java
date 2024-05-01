package stringcalculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private static final Pattern COSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");

    public int add(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        Matcher m = COSTOM_DELIMITER_PATTERN.matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            String[] tokens = m.group(2).split(customDelimiter);
            return Arrays.stream(tokens)
                    .map(PositiveNumber::new)
                    .reduce(PositiveNumber.ZERO, PositiveNumber::sum).intValue();
        }

        return Arrays.stream(text.split("[,:]"))
                .map(PositiveNumber::new)
                .reduce(PositiveNumber.ZERO, PositiveNumber::sum).intValue();
    }
}
