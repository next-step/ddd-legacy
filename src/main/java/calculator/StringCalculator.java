package calculator;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculator {
    public int add(String numbers) {
        if (numbers == null || numbers.isEmpty()) {
            return 0;
        }

        String delimiter = "[,:\\n]";
        String numbersToCalculate = numbers;

        Matcher m = Pattern.compile("//(.)\n(.*)").matcher(numbers);
        if (m.find()) {
            delimiter = m.group(1);
            numbersToCalculate = m.group(2);
        }

        String[] numbersArray = numbersToCalculate.split(delimiter);

        return Arrays.stream(numbersArray)
                .mapToInt(Integer::parseInt)
                .peek(num -> {
                    if (num < 0) {
                        throw new RuntimeException("음수는 허용되지 않습니다.");
                    }
                })
                .sum();
    }
}
