package addstring;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringCalculator {

    private static final Pattern CUSTOM_DELIMITER_REGEX = Pattern.compile("//(.)\n(.*)");
    private static final String DEFAULT_DELIMITER = "[,:]";


    public StringCalculator() {
    }

    public int add(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }

        String[] stringNumberArray = splitStringToArrayByDelimiter(s);

        List<Number> numberList = Arrays.stream(stringNumberArray)
            .map(Number::new)
            .collect(Collectors.toList());
        Numbers numbers = new Numbers(numberList);

        return numbers.sum();
    }

    private String[] splitStringToArrayByDelimiter(String s) {
        Matcher m = CUSTOM_DELIMITER_REGEX.matcher(s);
        if (m.find()) {
            String customDelimiter = m.group(1);
            return m.group(2).split(customDelimiter);
        }
        return s.split(DEFAULT_DELIMITER);

    }

}
