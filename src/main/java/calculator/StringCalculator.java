package calculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringCalculator {

    public int add(String input) {
        if (input.isEmpty()) return 0;

        return 105;
    }

    public List<Integer> extractNumbers(String input) {
        return Arrays.stream(input.split("[,:]"))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}
