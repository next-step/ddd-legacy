package calculator;

import java.util.Arrays;
import java.util.List;
import org.springframework.util.StringUtils;

public class StringCalculator {

    private static final String regex = ",|:";

    public int add(String text) {
        if (!StringUtils.hasLength(text)) {
            return 0;
        }

        return sum(splitNumbers(text));
    }

    private List<Integer> splitNumbers(String text) {
        String[] tokens = text.split(regex);
        return Arrays.stream(tokens)
                .map(Integer::parseInt)
                .toList();
    }

    private int sum(List<Integer> numbers) {
        return numbers.stream()
                .mapToInt(Integer::intValue)
                .sum();
    }
}
