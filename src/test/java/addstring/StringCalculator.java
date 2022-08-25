package addstring;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private static final String CUSTOM_DELIMITER_REGEX = "//(.)\n(.*)";
    private static final String DEFAULT_DELIMITER = "[,:]";

    public StringCalculator() {
    }

    public int add(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }

        String[] stringNumberArray = splitStringToArrayByDelimiter(s);

        Number number = new Number();
        return number.convertStringNumbersToIntSum(stringNumberArray);
    }

    private String[] splitStringToArrayByDelimiter(String s) {
        Matcher m = Pattern.compile(CUSTOM_DELIMITER_REGEX).matcher(s);
        if (m.find()) {
            String customDelimiter = m.group(1);
            return m.group(2).split(customDelimiter);
        }
        return s.split(DEFAULT_DELIMITER);

    }

}
