package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringCalculator {

    public int add(String input) {
        if (input.isEmpty()) return 0;
        List<Integer> numbers = extractNumbers(input);

        return numbers.stream().reduce(Integer::sum).orElseThrow(RuntimeException::new);
    }

    protected List<Integer> extractNumbers(String input) {
        return Arrays.stream(input.split("[,:]"))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}
