package calculator;

import java.util.Arrays;
import java.util.List;
import org.springframework.util.StringUtils;

public class StringCalculator {

    public int add(String text) {
        if (!StringUtils.hasLength(text)) {
            return 0;
        }

        String[] tokens = SplitterUtils.split(text);
        return sum(toIntegers(tokens));
    }

    private List<Integer> toIntegers(String[] tokens) {
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
