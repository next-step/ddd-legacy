package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");

    public int addExtractedNumberWithDelimiter(String text) throws RuntimeException {

        if (text == null || text.isEmpty()) return 0;

        String[] tokens = extractNumbers(text);

        return Arrays.stream(tokens)
                .map(token -> new PositiveStringNumber(token).getNumber())
                .reduce(Integer::sum)
                .get();
    }

    private String[] extractNumbers(String text) {
        String delimiter = ",|:";
        String numberString = text;

        Matcher m = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (m.find()) {
            delimiter = m.group(1);
            numberString = m.group(2);
        }
        return numberString.split(delimiter);
    }

}
