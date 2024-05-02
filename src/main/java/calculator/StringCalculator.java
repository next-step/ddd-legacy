package calculator;

import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    private static final String DEFAULT_DELIMITER = ",|:";

    public StringCalculator() {
    }

    public int add(String text) {

        if (Objects.isNull(text) || text.isBlank()) {
            return 0;
        }
        String[] tokens = getValue(text);
        String[] numbers = Arrays.stream(tokens)
                .filter(this::isNumeric)
                .filter(this::isPositiveNumber)
                .toArray(String[]::new);


        return sum(numbers);

    }

    private String[] getValue(String text) {
        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(text);
        if (m.find()) {
            String customDelimiter = m.group(1);
            return m.group(2).split(customDelimiter);
        }

        return text.split(DEFAULT_DELIMITER);
    }

    private boolean isNumeric(String number) {
        boolean result = number.matches("[0-9]+");

        if(!result){
            throw new RuntimeException("숫자가 아닙니다.");
        }
        return true;
    }

    private boolean isPositiveNumber(String number) {
        int value = Integer.parseInt(number);
        if(value < 0){
            throw new RuntimeException("음수는 처리할 수 없습니다.");
        }
        return true;
    }

    private int sum(String[] numbers) {
        return Arrays.stream(numbers)
                .mapToInt(Integer::parseInt)
                .sum();
    }


}
