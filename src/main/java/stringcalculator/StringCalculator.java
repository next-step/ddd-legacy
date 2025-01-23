package stringcalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {

    private static final String DEFAULT_DELIMITER = ",|:";
    private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.)\n(.*)");

    public int add(String text) {
        if (text == null || text.isBlank()) {
            return 0;
        }
        List<Integer> numbers = convertToIntArray(text);
        return sum(numbers);
    }

    private List<Integer> convertToIntArray(String text) {
        String[] textArray = split(text);

        List<Integer> numbers = new ArrayList<Integer>();
        for (String data : textArray) {
            int number = validate(data);
            numbers.add(number);
        }
        return numbers;
    }

    private int validate(String data) {
        int number = Integer.parseInt(data);
        if (number < 0) {
            throw new RuntimeException("음수는 계산할 수 없습니다.");
        }
        return number;
    }

    private String[] split(String text) {
        Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(text);
        if (matcher.find()) {
            String customDelimiter = matcher.group(1);
            return matcher.group(2).split(customDelimiter);
        }
        return text.split(DEFAULT_DELIMITER);
    }

    private int sum(List<Integer> numbers) {
        return numbers.stream()
                .mapToInt(Integer::intValue)
                .sum();
    }
}
