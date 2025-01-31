package stringcalculator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private final String COMMON_DELIMITER_PATTERN = "[,:]";
    private final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");

    public int add(String text) {
        if(isEmptyOrNull(text)) {
            return 0;
        }

        String[] numbers = stringSplit(text);

        String[] checkedNumbers = checkNegativeNumbers(numbers);

        return sum(checkedNumbers);
    }

    private boolean isEmptyOrNull(String text) {
        return text == null || text.isEmpty();
    }

    private String[] stringSplit(String text) {

        Matcher m = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            return m.group(2).split(customDelimiter);
        }

        return text.split(COMMON_DELIMITER_PATTERN);
    }

    private String[] checkNegativeNumbers(String[] numbers) {
        for(String number : numbers){
            int num = Integer.parseInt(number);
            checkNegativeNumber(num);
        }

        return numbers;
    }

    private void checkNegativeNumber(int num) {
        if(num < 0){
            throw new RuntimeException();
        }
    }

    private int sum (String[] numbers) {
        int result = 0;

        for(String number : numbers) {
            result += Integer.parseInt(number);
        }

        return result;
    }
}
