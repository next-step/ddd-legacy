package calculator;

import java.util.Arrays;
import java.util.List;
import org.springframework.util.StringUtils;

public class StringCalculator {

    private final int ZERO_VALUE = 0;

    public int add(String text) {
        if (!StringUtils.hasLength(text)) {
            return ZERO_VALUE;
        }

        String[] tokens = SplitterUtils.split(text);
        return sum(toNumbers(tokens));
    }

    private List<Integer> toNumbers(String[] tokens) {
        return Arrays.stream(tokens)
                .map(this::toNumber)
                .toList();
    }

    private int toNumber(String token) {
        validateToken(token);
        return Integer.parseInt(token);
    }

    private void validateToken(String token) {
        if (isNegative(token)) {
            throw new RuntimeException(token + "is Negative");
        }
    }

    private boolean isNegative(String token) {
        return Integer.parseInt(token) < 0;
    }

    private int sum(List<Integer> numbers) {
        return numbers.stream()
                .mapToInt(Integer::intValue)
                .sum();
    }
}
