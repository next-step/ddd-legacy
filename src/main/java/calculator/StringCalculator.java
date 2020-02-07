package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringCalculator {

    public int add(String input) {
        if (input == null) return 0;
        if (input.isEmpty()) return 0;

        return extractNumbers(input).stream()
                .reduce(Integer::sum)
                .orElseThrow(RuntimeException::new);
    }

    protected List<Integer> extractNumbers(String input) {
        return Arrays.stream(input.split("[,:]"))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}
